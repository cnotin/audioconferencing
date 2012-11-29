package se.ltu.M7017E.lab2.client;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab2.client.audio.ReceiverPipeline;
import se.ltu.M7017E.lab2.client.ui.Gui;
import se.ltu.M7017E.lab2.common.Contact;

public class App {
	@Getter
	private ControlChannel control;
	@Getter
	private List<Contact> contacts = new LinkedList<Contact>();
	@Getter
	private Contact me = new Contact("lab2 client", "lab2.client.lan");
	@Setter
	private Gui gui;

	private ReceiverPipeline receiver;

	public App() {
		control = new ControlChannel(this);
		new Thread(control).start();

		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=3",
				"--gst-debug-no-color" });
		receiver = new ReceiverPipeline();
		receiver.play();
	}

	public void joinRoom(int roomId) {
		receiver.joinRoom(roomId);
		// TODO: connect sender too
	}

	public void leaveRoom(int roomId) {
		receiver.leaveRoom(roomId);
		// TODO: disconnect sender too
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
