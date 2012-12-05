package se.ltu.M7017E.lab2.client;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab2.client.audio.ReceiverPipeline;
import se.ltu.M7017E.lab2.client.audio.SenderPipeline;
import se.ltu.M7017E.lab2.client.ui.Gui;
import se.ltu.M7017E.lab2.common.Contact;

@Getter
public class App {
	@Getter
	private ControlChannel control;
	private List<Contact> contacts = new LinkedList<Contact>();
	private Contact me = new Contact("lab2 client", "lab2.client.lan");

	@Setter
	@Getter
	private Gui gui;

	private ReceiverPipeline receiver;
	private SenderPipeline sender;

	public App() {
		control = new ControlChannel(this);
		new Thread(control).start();

		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=3",
				"--gst-debug-no-color" });
		receiver = new ReceiverPipeline();
		receiver.play();

		sender = new SenderPipeline();
	}

	public void joinRoom(int roomId) {
		long mySSRC = sender.streamTo(roomId);
		receiver.receiveFromRoom(roomId, mySSRC);
	}

	public void leaveRoom(int roomId) {
		sender.stopStreamingTo(roomId);
		receiver.stopRoomReceiving(roomId);
	}

	public Contact findcontactsByName(String name) {
		for (Contact contact : contacts) {
			if (contact.getName().equals(name)) {
				return contact;
			}
		}

		return null;
	}

}