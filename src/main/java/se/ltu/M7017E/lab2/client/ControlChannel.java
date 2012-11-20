package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Manage the control channel
 */
public class ControlChannel implements Runnable {
	private App app;
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private boolean quit = false; // set to true to exit thread

	public ControlChannel(App app) {
		System.out.println("Creating control channel");
		this.app = app;

		try {
			socket = new Socket(InetAddress.getByName("localhost"), 5000);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!quit) {
			String message = null;
			try {
				message = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (message != null) {
				System.out.println("Got raw msg: " + message);
				caseMessage(message);
			} else {
				quit = true;
			}
		}
	}

	public void caseMessage(String message) {
		if (message.startsWith("JOIN")) {
			String[] tokens = message.split(",");
			Friend newFriend = new Friend(tokens[1], tokens[2],
					Integer.valueOf(tokens[3]));

			Friend oldFriend = app.findFriendByName(newFriend.getName());
			if (oldFriend == null) {
				System.out.println("New friend: " + newFriend);
				app.getFriends().add(newFriend);
			}
		} else if (message.startsWith("UPDATE_STREAM")) {
			String[] tokens = message.split(",");
			Friend friend = app.findFriendByName(tokens[1]);
			if (friend == null) {
				System.err
						.println("Got an UPDATE_STREAM message for an unknown friend called '"
								+ tokens[1] + "'");
			} else {
				// update the friend
				friend.setIp(tokens[2]);
				friend.setPort(new Integer(tokens[3]));

				System.out.println("The friend '" + friend.getName()
						+ "' updated stream to " + friend.getIp() + ":"
						+ friend.getPort());

				// TODO connect to the new stream and disconnect old
			}
		} else if (message.startsWith("UPDATE_NAME")) {
			String[] tokens = message.split(",");
			Friend friend = app.findFriendByName(tokens[1]);
			if (friend == null) {
				System.err
						.println("Got an UPDATE_NAME message for an unknown friend called '"
								+ tokens[1] + "'");
			} else {
				// update the friend
				System.out.println("The friend '" + friend.getName()
						+ "' updated name to '" + tokens[2] + "'");

				friend.setName(tokens[2]);
			}
		} else if (message.startsWith("BYE")) {
			String[] tokens = message.split(",");
			Friend friend = app.findFriendByName(tokens[1]);
			if (friend == null) {
				System.err
						.println("Got a BYE message for an unknown friend called '"
								+ tokens[1] + "'");
			} else {
				// remove the friend
				app.getFriends().remove(friend);

				// TODO disconnect old stream also
			}
		}
	}

	public void whosThere() {
		out.println("WHOS_THERE");
	}

	public void iAm(Friend me) {
		out.println("I_AM," + me.getName() + "," + me.getIp() + ","
				+ me.getPort());
	}

	public void bye(Friend me) {
		out.println("BYE," + me.getName());
	}

	public void ping(Friend me) {
		out.println("PING," + me.getName());
	}
}
