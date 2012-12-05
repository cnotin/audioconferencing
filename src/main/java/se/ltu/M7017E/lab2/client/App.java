package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab2.client.audio.ReceiverPipeline;
import se.ltu.M7017E.lab2.client.audio.SenderPipeline;
import se.ltu.M7017E.lab2.client.ui.Gui;
import se.ltu.M7017E.lab2.common.Contact;
import se.ltu.M7017E.lab2.common.messages.Hello;

@Getter
public class App {
	private ControlChannel control;
	private List<Contact> contacts = new LinkedList<Contact>();
	private String username;

	@Setter
	private Gui gui;

	private ReceiverPipeline receiver;
	private SenderPipeline sender;

	public App() {
		// ######### BUSINESS LOGIC ############
		fetchUsername();

		// ######### COMMUNICATION WITH SERVER ###########
		control = new ControlChannel(this);
		new Thread(control).start();

		// say hello :)
		control.send(new Hello(this.username).toString());

		// ############ GSTREAMER STUFF ###############
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

	public void fetchUsername() {
		/*
		 * algo: check if file exists, then check if there is a name inside. If
		 * any of these two tests fail: ask for a name
		 */
		File file = new File("name.txt");
		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				br = new BufferedReader(fr);
				String s = br.readLine();
				System.out.println("hello" + s);
				if (s != null) {
					this.username = s;
					fr.close();
					return;
				}
				fr.close();
			} catch (IOException e) {
				System.err
						.println("Got a problem while fetching username from file");
				e.printStackTrace();
			}
		}

		// fetching failed, ask for a name
		String name;
		do {
			name = JOptionPane.showInputDialog(null, "Choose a name",
					"Name selection", JOptionPane.QUESTION_MESSAGE);
		} while (name == null);
		this.username = name;

		// save it
		try {
			FileWriter myFile = new FileWriter("name.txt");
			myFile.write(name);
			myFile.close();
		} catch (IOException e) {
			System.err.println("Got a problem while saving username to file");
		}
	}
}