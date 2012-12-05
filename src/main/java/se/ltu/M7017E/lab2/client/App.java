package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab2.client.audio.ReceiverPipeline;
import se.ltu.M7017E.lab2.client.audio.SenderPipeline;
import se.ltu.M7017E.lab2.client.ui.Gui;
import se.ltu.M7017E.lab2.common.Room;
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
	@Getter
	private Map<Integer, Room> allRooms = new HashMap<Integer, Room>();
	private Map<Integer, Room> myRooms = new HashMap<Integer, Room>();
	// public List<String> msgFromserver = new ArrayList<String>();
	@Setter
	@Getter
	private boolean serverIsWriting = false;
	private List<String> msgFromServer = new LinkedList<String>();
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
		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=2",
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
		// TODO: connect sender too
		getControl().send("JOIN," + roomId);
		// updateallRoomsList();
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

	public void createAllRoomList() {
		// String OneRoom[];
		Set<String> roomContactList = new TreeSet<String>();
		int index = 0;

		boolean server = true;
		boolean test = true;

		if (server) {
			try {
				Thread.pause(); // the thread waits
				getControl().send("LIST");

				while (test) {
					test = serverIsWriting;
				}
				updateMsgFromServer(control.msgList);

				// int count = msgFromserver.size();
				for (String oneRoom : msgFromServer) {
					oneRoom = msgFromServer.iterator().next();
					String split[] = oneRoom.split(",", 0);

					Room newRoom = new Room();
					newRoom.setId(Integer.parseInt(split[1]));
					for (int i = 2; i < split.length; i++) {
						roomContactList.add(split[i]);
					}
					newRoom.setAudience(roomContactList);
					allRooms.put(oneRoom.indexOf(oneRoom), newRoom);
				}
				// for (int i = 0; i < count; i++) {
				// OneRoom = msgFromserver.get(i).split(",", 0);
				// newRoom = new Room();
				// newRoom.setId(Integer.parseInt(OneRoom[1]));
				//
				// roomContactList = new HashSet<Contact>();
				// for (int k = 2; k < OneRoom.length; k++) {
				// roomContactList.add(new Contact(OneRoom[k],
				// "0240649471"));
				// }
				// newRoom.setAudience(roomContactList);
				// allRooms.put(i, newRoom);
				// }
				control.notifyAll(); // notify the others threads
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error when the thread is waiting");
			} catch (NoSuchElementException e1) {
				e1.printStackTrace();
				System.out.println("End of the Server message");
			} catch (NumberFormatException n) {
				n.printStackTrace();
				System.out.println("Number error for parseint");
			}
			// Debug : create the list when the server is down
		} else {
			// TODO if the server is down, create a list
			for (int i = 0; i < 3; i++) {
				Room newRoom = new Room();
				newRoom.setId(i);
				if (i < 2) {
					for (int j = 0; j < 2; j++) {
						roomContactList.add("Contact " + i);
					}
				} else {
					roomContactList.add("Contact 1");
				}
				newRoom.setAudience(roomContactList);
				allRooms.put(i, newRoom);
			}
		}
		index = 1;
	}

	// public void createMyRoomsList() {
	// for (int i = 0; i < allRooms.size(); i++) {
	// if (allRooms.get(i).isWithin(me) {
	// myRooms.put(i, allRooms.get(i));
	// }
	// }
	// }
	/*
	 * public void updateallRoomsList() { String readRoomInfo; ArrayList<String>
	 * roomsInfoString = new ArrayList<String>(); String roomComponents[];
	 * Set<Contact> roomContactList; Room newRoom; int index = 0;
	 * 
	 * // Ask to the server to create the Roomlist try {
	 * getControl().send("LIST"); readRoomInfo =
	 * this.getControl().getIn().readLine(); do {
	 * roomsInfoString.add(readRoomInfo); index++; readRoomInfo =
	 * this.getControl().getIn().readLine(); } while
	 * (readRoomInfo.contains("AUDIENCE")); } catch (IOException e) {
	 * e.printStackTrace(); System.out
	 * .println("Erreur d'IO lors de la lecture du Message server"); }
	 * 
	 * for (int i = 0; i < roomsInfoString.size(); i++) { roomComponents =
	 * roomsInfoString.get(i).split(",", 0); newRoom = new Room();
	 * 
	 * if (roomComponents[1].contains("AUDIENCE")) {
	 * newRoom.setId(Integer.parseInt(roomComponents[1])); roomContactList = new
	 * HashSet<Contact>(); for (int k = 2; k < roomComponents.length; k++) {
	 * roomContactList.add(new Contact(roomComponents[k], "0240649471")); }
	 * newRoom.setAudience(roomContactList); allRooms.put(i, newRoom); } } //
	 * Debug : create the list when the server is down for (int i = 0; i < 3;
	 * i++) { newRoom = new Room(); roomContactList = new HashSet<Contact>();
	 * newRoom.setId(i); if (i < 2) { for (int j = 0; j < 2; j++) { Contact
	 * contact = new Contact(); contact.setName("contact " + j);
	 * contact.setIp("IP TEST"); roomContactList.add(contact); } } else {
	 * Contact contact = new Contact(); contact.setName("contact 2");
	 * contact.setIp("IP TEST"); roomContactList.add(contact); }
	 * newRoom.setAudience(roomContactList); allRooms.put(i, newRoom); } }
	 */
	public void updateMsgFromServer(List<String> CloneArray) {
		msgFromServer.clear();
		for (String string : CloneArray) {
			msgFromServer.add(string);
		}
		// msgFromserver = CloneArray;
	}
}

// ArrayList<String> readLine = new ArrayList<String>();
// int index = 0;
//
// try {
// getControl().send("LIST");
// readLine.add(index, getControl().getIn().readLine());
// if (readLine.get(index).contains("ROOMS_START")) {
// readLine.add(index, getControl().getIn().readLine());
// } else {
// while (readLine.get(index).contains("AUDIENCE")) {
// index++;
// readLine.add(index, getControl().getIn().readLine());
// }
// }
// } catch (IOException e) {
// e.printStackTrace();
// }

// allRooms.clear();
// for (int i = 0; i < readLine.size(); i++) {
//
// Room newRoom = new Room();
// String OneRoom[];
// Set<Contact> contactsRoom;
//
// OneRoom = readLine.get(i).split(",", 0);
// if (OneRoom[0].contains("AUDIENCE")) {
// contactsRoom = new HashSet<Contact>();
// for (int k = 2; k < OneRoom.length; k++) {
// contactsRoom.add(new Contact(OneRoom[k], "0240649471"));
// }
// newRoom.setId(Integer.parseInt(OneRoom[1]));
// newRoom.setAudience(contactsRoom);
// allRooms.put(i, newRoom);// allRooms.clear();
// for (int i = 0; i < readLine.size(); i++) {
//
// Room newRoom = new Room();
// String OneRoom[];
// Set<Contact> contactsRoom;
//
// OneRoom = readLine.get(i).split(",", 0);
// if (OneRoom[0].contains("AUDIENCE")) {
// contactsRoom = new HashSet<Contact>();
// for (int k = 2; k < OneRoom.length; k++) {
// contactsRoom.add(new Contact(OneRoom[k], "0240649471"));
// }
// newRoom.setId(Integer.parseInt(OneRoom[1]));
// newRoom.setAudience(contactsRoom);
// allRooms.put(i, newRoom);
// }get
// }
// }
// }

// }
// }
// }
// }

// public Map<Integer, Room> createAllRoomList() {
//
// String readRoomInfo;
// ArrayList<String> roomsInfoString = new ArrayList<String>();
// String roomComponents[];
// Set<Contact> roomContactList;
// Room newRoom;
// int index = 0;
//
// boolean server = true;
//
// if (server) {
// // send message to the server
// this.getControl().send("LIST");
// // Ask to the server to create the Roomlist
// try {
// readRoomInfo = this.getControl().getIn().readLine();
// do {
// roomsInfoString.add(readRoomInfo);
// index++;
// readRoomInfo = this.getControl().getIn().readLine();
// // if (readRoomInfo.contains("AUDIENCE")) {
// // index++;
// // index--;
// // }
// } while (readRoomInfo.contains("AUDIENCE"));
// } catch (IOException e) {
// e.printStackTrace();
// System.out
// .println("Erreur d'IO lors de la lecture du Message server");
// }
// // split the room name to have the ID and the audience of each
// // room
// for (int i = 0; i < roomsInfoString.size(); i++) {
// roomComponents = roomsInfoString.get(i).split(",", 0);
// // ############ DEBUG
// // System.out.println("\t\t RoomComponent : " + i + " => ");
// // for (int j = 0; j < roomComponents.length; j++) {
// // System.out.println("\t\t\t " + roomComponents[j]);
// // }
// // ############ END DEBUG
// newRoom = new Room();
// roomContactList = new HashSet<Contact>();
//
// newRoom.setId(Integer.parseInt(roomComponents[1]));
// for (int k = 2; k < roomComponents.length; k++) {
// roomContactList.add(new Contact(roomComponents[k],
// "0240649471"));
// }
// newRoom.setAudience(roomContactList);
// allRooms.put(i, newRoom);
// }
// // Debug : create the list when the server is down
// } else {
// // TODO if the server is down, create a list
// for (int i = 0; i < 3; i++) {
// newRoom = new Room();
// roomContactList = new HashSet<Contact>();
// newRoom.setId(i);
// if (i < 2) {
// for (int j = 0; j < 2; j++) {
// Contact contact = new Contact();
// contact.setName("contact " + j);
// contact.setIp("IP TEST");
// roomContactList.add(contact);
// }
// } else {
// Contact contact = new Contact();
// contact.setName("contact 2");
// contact.setIp("IP TEST");
// roomContactList.add(contact);
// }
// newRoom.setAudience(roomContactList);
// allRooms.put(i, newRoom);
// }
// }
// return allRooms;
// }
