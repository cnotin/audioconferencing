package se.ltu.M7017E.lab2.presenceserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client implements Runnable {
	private Socket socket;
	private App app;
	private BufferedReader in;
	private PrintStream out;
	private Friend me = null;
	/**
	 * Set to true to exit thread
	 */
	private boolean quit = false;

	public Client(App app, Socket socket) {
		System.out.println("Nouveau client " + socket.getInetAddress());
		this.socket = socket;
		this.app = app;

		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String message = null;
		while (!quit) {
			try {
				message = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (message == null) {
				/*
				 * usually means that the client disconnected without a proper
				 * BYE, so we craft it manually
				 */
				message = "BYE";
			}
			// do something with the message
			caseMessage(message);
		}
	}

	public void caseMessage(String message) {
		if (message.startsWith("WHOS_THERE")) {
			for (Friend friend : app.getFriends()) {
				send("THERE_IS," + friend.getName() + "," + friend.getIp()
						+ "," + friend.getPort());
			}

		} else if (message.startsWith("I_AM")) {
			String[] tokens = message.split(",");
			Friend friend = new Friend(tokens[1], tokens[2],
					Integer.valueOf(tokens[3]), this);

			if (me != null) {
				/*
				 * A client changed something (could be one or several
				 * properties). So we remove the old object and put the new one.
				 */
				app.getFriends().remove(me);

				// name changed?
				if (!friend.getName().equals(me.getName())) {
					System.out.println("Update name: " + me.getName() + " -> "
							+ friend.getName());
					app.broadcast("UPDATE_NAME," + me.getName() + ","
							+ friend.getName());
				}

				// IP or port changed?
				if (!friend.getIp().equals(me.getIp())
						|| friend.getPort() != me.getPort()) {
					System.out.println("Update stream: " + me.getIp() + ":"
							+ me.getPort() + " -> " + friend.getIp() + ":"
							+ friend.getPort());
					app.broadcast("UPDATE_STREAM," + friend.getName() + ","
							+ friend.getIp() + "," + friend.getPort());
				}
			} else {
				// simply a new friend
				System.out.println("New friend:" + friend);
				app.broadcast("THERE_IS," + friend.getName() + ","
						+ friend.getIp() + "," + friend.getPort());
			}

			me = friend; // there's a new me (modified or real new client)
			app.getFriends().add(me);
		} else if (message.startsWith("BYE")) {
			System.out.println("Bye " + me);
			app.getFriends().remove(me);
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			app.broadcast("BYE," + me.getName());

			quit = true;
		} else if (message.startsWith("PING")) {
			// TODO implement a last seen property
		} else {
			System.out.println("Unknown message !");
		}
	}

	/**
	 * Send the message.
	 * 
	 * @param message
	 *            without any formatting (no '\n' at the end for example)
	 */
	public void send(String message) {
		out.println(message);
	}
}
