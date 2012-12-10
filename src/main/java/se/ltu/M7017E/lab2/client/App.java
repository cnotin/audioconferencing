package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import se.ltu.M7017E.lab2.messages.AnswerCall;
import se.ltu.M7017E.lab2.messages.Call;
import se.ltu.M7017E.lab2.messages.ConnectedList;
import se.ltu.M7017E.lab2.messages.Hello;
import se.ltu.M7017E.lab2.messages.Join;
import se.ltu.M7017E.lab2.messages.Joined;
import se.ltu.M7017E.lab2.messages.Leave;
import se.ltu.M7017E.lab2.messages.Left;
import se.ltu.M7017E.lab2.messages.ListMsg;
import se.ltu.M7017E.lab2.messages.StopCall;

@Getter
public class App {
	private static final String NAME_CONFIG_FILE = "name.txt";
	/** my list of contacts */
	private Set<String> contacts = new TreeSet<String>();
	/** the connected people from my list of contacts */
	private Set<String> connected = new HashSet<String>();
	/** own username */
	private String username;
	/** name of the guy we're currently calling */
	private String friend;

	/** User interface */
	@Setter
	private Gui gui;

	/** all the rooms in the server */
	private List<Room> allRooms = new LinkedList<Room>();
	/** only the rooms I'm currently in */
	private List<Room> myRooms = new LinkedList<Room>();

	/** buffer for receiving messages from server separated in several packets */
	@Setter
	private List<String> msgFromServer = new LinkedList<String>();

	/** TCP connection to server */
	private ControlChannel control;
	/** GStreamer pipeline to receive from rooms and contact */
	private ReceiverPipeline receiver;
	/** GStreamer pipeline to send to rooms and contact */
	private SenderPipeline sender;

	public App() {
		// ######### BUSINESS LOGIC ############
		fetchUsername(); // get my name from file
		fetchContacts(); // get my contacts list

		// ######### COMMUNICATION WITH SERVER ###########
		control = new ControlChannel(this);
		new Thread(control).start();

		// say hello :)
		control.send(new Hello(this.username).toString());

		// ############ GSTREAMER STUFF ###############
		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=2",
				"--gst-debug-no-color" });

		receiver = new ReceiverPipeline();
		sender = new SenderPipeline();
	}

	/**
	 * Update the allRooms variable when someone join a room in which the user
	 * is. If the user is not in the room, the tree is not updated since the
	 * Join message has not be received
	 * 
	 * @param joined
	 *            the message received from the server
	 */
	public void msg(Joined joined) {
		for (Room joinedRoom : allRooms) {
			if (joinedRoom.getId() == joined.getRoom()) {
				allRooms.get(allRooms.indexOf(joinedRoom)).getAudience()
						.add(joined.getName());
			}
		}
		gui.displayRoomList(allRooms);
	}

	/**
	 * Update the allRooms variable when someone left a room in which the user
	 * is. If the user is not in the room, the tree is not updated since the
	 * Left message has not be received
	 * 
	 * @param left
	 *            the message received from the server
	 */
	public void msg(Left left) {
		for (Room leftRoom : allRooms) {
			if (leftRoom.getId() == left.getRoom()) {
				allRooms.get(allRooms.indexOf(leftRoom)).getAudience()
						.remove(left.getName());
			}
		}
		gui.displayRoomList(allRooms);
	}

	/**
	 * Contact says that he ended the call.
	 * 
	 * @param stopCall
	 *            the received msg
	 */
	public void msg(StopCall stopCall) {
		stopCall();
	}

	/**
	 * Join a room (set up audio send/receive)
	 * 
	 * @param roomId
	 *            number from 1 to 254
	 */
	public void joinRoom(int roomId) {
		/*
		 * remember my SSRC to remove it from the incoming stream from multicast
		 * (prevents echo of my own voice)
		 */
		long mySSRC = sender.streamTo(roomId);
		receiver.receiveFromRoom(roomId, mySSRC);

		send(new Join(roomId));
		try {
			control.getRoomsListFinished().acquire();
		} catch (InterruptedException e) {
			System.err
					.println("This thread has been interrupted while waiting the end "
							+ "of a message from the server, message might be incomplete...");
			e.printStackTrace();
		}
		Room newRoom = updateAfterJoin(control.getUpdatedAudience());
		boolean createRoom = true;
		for (Room oldRoom : allRooms) {
			if (oldRoom.getId() == newRoom.getId()) {
				allRooms.set(allRooms.indexOf(oldRoom), newRoom);
				createRoom = false;
			}
		}
		if (createRoom) {
			allRooms.add(newRoom);
		}
	}

	/**
	 * Send a message to the server to leave the room and disconnect the user
	 * (shutdown audio)
	 * 
	 * @param roomId
	 *            the room to leave from 1 to 254
	 */
	public void leaveRoom(int roomId) {
		getControl().send(new Leave(roomId).toString());
		for (Room room : allRooms) {
			if (room.getId() == roomId) {
				allRooms.get(allRooms.indexOf(room)).getAudience()
						.remove(username);
			}
		}
		sender.stopStreamingToRoom(roomId);
		receiver.stopRoomReceiving(roomId);
	}

	/**
	 * Retrieve my username from config file. Ask it to the user if not already
	 * set and then save it.
	 */
	public void fetchUsername() {
		/*
		 * algo: check if file exists, then check if there is a name inside. If
		 * any of these two tests fail: ask for a name
		 */
		File file = new File(NAME_CONFIG_FILE);
		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s = br.readLine();
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
			FileWriter myFile = new FileWriter(NAME_CONFIG_FILE);
			myFile.write(name);
			myFile.close();
		} catch (IOException e) {
			System.err.println("Got a problem while saving username to file");
		}
	}

	/**
	 * Send a message to the server to try to call someone
	 * 
	 * @param contact
	 *            Name of the person to call
	 */
	public void askToCall(String contact) {
		if (contact.endsWith("(Disconnected)")) {
			contact = contact.substring(0, contact.length() - 15);
		}
		// open a local port for our (maybe) future conversation
		int port = this.receiver.receiveFromUnicast();
		send(new Call(username, contact, "0", port));
		// remember name of my friend I'm talking with
		this.friend = contact;
	}

	/**
	 * Start sending stream to someone for a call
	 * 
	 * @param ipReceiver
	 * @param port
	 */
	public void call(String ipReceiver, int port) {
		sender.streamTo(ipReceiver, port);

		gui.getCallBtn().setVisible(false);
		gui.getHangUpBtn().setVisible(true);
	}

	/**
	 * hang up. Stop the pipelines and send a message to the server for telling
	 * the other client the call is finished
	 */
	public void askToStopCall() {
		stopCall();

		send(new StopCall(friend));
	}

	/**
	 * Stop streaming from/to friend and update UI buttons.
	 */
	public void stopCall() {
		// stop streaming from friend
		receiver.stopUnicastReceiving();
		// stop streaming to friend
		sender.stopStreamingToUnicast();

		gui.getCallBtn().setVisible(true);
		gui.getHangUpBtn().setVisible(false);
	}

	/**
	 * send an AnswerCall message to the server which transmit to the other
	 * client. Used to tell if we accept or refuse the call
	 * 
	 * @param String
	 *            answer : "yes" or "no"
	 * @param Call
	 *            call : the Call message received from the server
	 */
	public void answerCall(String answer, Call call) {
		int port = -1;

		if (answer.equals("yes")) {
			port = receiver.receiveFromUnicast();
			this.friend = call.getSender();

			gui.getCallBtn().setVisible(false);
			gui.getHangUpBtn().setVisible(true);
		}

		send(new AnswerCall(port, call.getSender(), call.getReceiver(), answer,
				"0"));
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

	/**
	 * Save the contact List
	 */
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

	/**
	 * Add a contact in the list of contacts
	 */
	public void addContact(String contact) {
		this.contacts.add(contact);
		saveContacts();
	}

	/**
	 * Remove a contact from the list of contacts.
	 */
	public void removeContact(String contact) {
		if (contact.endsWith("(Disconnected)")) {
			contact = contact.substring(0, contact.length() - 15);
		}

		this.contacts.remove(contact);
		saveContacts();
	}

	/**
	 * Set the people connected to the server list with the ConnectedMessage
	 * received from the server
	 */
	public void setConnected(String connectedMessage) {
		ConnectedList connectedList = ConnectedList
				.fromString(connectedMessage);
		connected = connectedList.getConnected();

		if (gui != null) {
			gui.refreshContactsList();
		}
	}

	/**
	 * Initiate the allRooms global variable, which contains all the rooms where
	 * there is at least one person
	 */
	public void createAllRoomList() {
		allRooms.clear();
		List<Integer> indexRoom = new ArrayList<Integer>();
		boolean server = true;

		if (server) {
			try {
				// ask the server for the list
				send(new ListMsg());
				// wait for the semaphore
				control.getRoomsListFinished().acquire();
				updateMsgFromServer(control.getMsgList());
				for (String oneRoom : msgFromServer) {
					Room newRoom = new Room();
					Set<String> roomContactList = new TreeSet<String>();
					String split[] = oneRoom.split(",", 0);

					newRoom.setId(Integer.parseInt(split[1]));
					indexRoom.add(newRoom.getId());
					for (int i = 2; i < split.length; i++) {
						roomContactList.add(split[i]);
					}
					newRoom.setAudience(roomContactList);
					allRooms.add(newRoom);
				}
				for (int i = 1; i < 255; i++) {
					if (!indexRoom.contains(i)) {
						Room newRoom = new Room(i);
						allRooms.add(newRoom);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NoSuchElementException e1) {
				e1.printStackTrace();
			} catch (NumberFormatException n) {
				n.printStackTrace();
			}
			// Debug : create the list when the server is down
		} else {
			for (int i = 0; i < 3; i++) {
				Room newRoom = new Room();
				Set<String> roomContactList = new TreeSet<String>();
				newRoom.setId(i);
				if (i < 2) {
					for (int j = 0; j < 2; j++) {
						roomContactList.add("Contact " + i);
					}
				} else {
					roomContactList.add("Contact 1");
				}
				newRoom.setAudience(roomContactList);
				allRooms.add(newRoom);
			}
		}
	}

	/**
	 * Update a room when the user just joined it.
	 * 
	 * @param newAudience
	 *            the new contact list in the room joined.
	 * @return the new state of the room joined
	 */
	public Room updateAfterJoin(String newAudience) {
		Room updatedRoom = new Room();
		Set<String> updatedContactList = new TreeSet<String>();
		String splitnewAudience[] = newAudience.split(",", 0);

		updatedRoom.setId(Integer.parseInt(splitnewAudience[1]));
		for (int i = 2; i < splitnewAudience.length; i++) {
			String string = splitnewAudience[i];
			updatedContactList.add(string);
		}
		updatedRoom.setAudience(updatedContactList);
		return updatedRoom;
	}

	/**
	 * Update a room when the user just left it.
	 * 
	 * @param newAudience
	 *            the new contact list in the room joined.
	 * @return the new state of the room joined
	 */
	public Room updateAfterLeave(String leftMessage) {
		String splitMessage[];
		String deleteContact;
		Set<String> updatedContactList = new TreeSet<String>();
		Room updatedRoom = new Room();

		splitMessage = leftMessage.split(",", 0);
		deleteContact = splitMessage[2];
		updatedRoom.setId(Integer.parseInt(splitMessage[1]));
		// for all the rooms
		for (Room currentRoom : allRooms) {
			if (currentRoom.getId() == updatedRoom.getId()) {
				for (String contactName : currentRoom.getAudience()) {
					if (contactName != deleteContact) {
						updatedContactList.add(contactName);
					}
				}
			}
		}
		updatedRoom.setAudience(updatedContactList);
		return updatedRoom;
	}

	/**
	 * Update the message received from the server when asking for a list of
	 * rooms.
	 * 
	 * @param CloneList
	 *            the message List from the server
	 */
	public void updateMsgFromServer(List<String> CloneList) {
		msgFromServer.clear();
		for (String string : CloneList) {
			msgFromServer.add(string);
		}
	}

	/**
	 * Update the list which contains all the room the user is in.
	 * 
	 * @param allRoomList
	 *            the list with all the Rooms currently in use
	 */
	public void createMyRooms(List<Room> allRoomList) {
		Room mynewRoom;
		myRooms.clear();
		for (Room rooms : allRoomList) {
			if (rooms.getAudience().contains(username)) {
				mynewRoom = new Room();
				mynewRoom.setAudience(rooms.getAudience());
				mynewRoom.setId(rooms.getId());
				myRooms.add(mynewRoom);
			}
		}
	}

	public void send(Object msg) {
		this.control.send(msg.toString());
	}
}
