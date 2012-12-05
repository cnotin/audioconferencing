package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab2.client.audio.ReceiverPipeline;
import se.ltu.M7017E.lab2.client.audio.SenderPipeline;
import se.ltu.M7017E.lab2.client.ui.Gui;
import se.ltu.M7017E.lab2.common.messages.AnswerCall;
import se.ltu.M7017E.lab2.common.messages.Call;
import se.ltu.M7017E.lab2.common.messages.Hello;
import se.ltu.M7017E.lab2.common.messages.Left;

@Getter
public class App {
	private ControlChannel control;
	private Set<String> contacts = new TreeSet<String>();
	private String username;

	@Setter
	private Gui gui;

	private ReceiverPipeline receiver;
	private SenderPipeline sender;

	public App() {
		// ######### BUSINESS LOGIC ############
		fetchUsername();
		fetchContacts();

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

	public void msg(Hello hello) {
		// TODO: update tree
	}

	public void msg(Left left) {
		// TODO: update tree
	}

	public void joinRoom(int roomId) {
		long mySSRC = sender.streamTo(roomId);
		receiver.receiveFromRoom(roomId, mySSRC);
	}

	public void leaveRoom(int roomId) {
		sender.stopStreamingTo(roomId);
		receiver.stopRoomReceiving(roomId);
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
				if (s != null && !s.isEmpty()) {
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

	/**
	 * send a message to the server
	 */
	public void askToCall(String receiver) {
		Call call = new Call();
		call.setSender(username);
		call.setReceiver(receiver);
		control.send(call.toString());
	}

	public void call(String ip, int port, String ipReceiver) {
		System.out.println("IP: " + ipReceiver);
		sender.streamTo(ipReceiver, port);
	}

	public String buildAnswer(String answer, Call call) {
		AnswerCall answerCall = new AnswerCall();
		answerCall.setAnswer(answer);
		answerCall.setReceiver(call.getReceiver());
		answerCall.setSender(call.getSender());
		answerCall.setIpReceiver("0");
		String port = Integer.toString(control.getApp().getReceiver()
				.receiveFromUnicast());
		answerCall.setPortReceiver(port);
		return answerCall.toString();
	}

	public void answerCall(String answer, Call call) {
		control.send(buildAnswer(answer, call));
	}

	/**
	 * Fetch all the contacts from the file.
	 */
	private void fetchContacts() {
		File file = new File("contacts.txt");
		if (!file.exists()) // check if the file already exist, else the
							// file is created
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		String s;
		try {
			FileReader fr = new FileReader("contacts.txt");
			BufferedReader br = new BufferedReader(fr);
			while ((s = br.readLine()) != null) {
				contacts.add(s);
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveContacts() {
		FileWriter file;
		try {
			file = new FileWriter("contacts.txt");
			BufferedWriter out = new BufferedWriter(file);
			for (String contact : contacts) {
				out.write(contact);
				out.newLine();
			}
			out.close();
			file.close();
		} catch (IOException e) {
			System.err.println("Got a problem while saving contacts list");
			e.printStackTrace();
		}
	}

	public void addContact(String username) {
		this.contacts.add(username);
		saveContacts();
	}

	public void removeContact(String username) {
		this.contacts.remove(username);
		saveContacts();
	}
}
