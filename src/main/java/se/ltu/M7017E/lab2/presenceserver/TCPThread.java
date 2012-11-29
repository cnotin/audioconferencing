package se.ltu.M7017E.lab2.presenceserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import se.ltu.M7017E.lab2.common.messages.Join;
import se.ltu.M7017E.lab2.common.messages.Leave;

public class TCPThread implements Runnable {
	private App app;
	private BufferedReader in;
	private PrintStream out;
	private Friend me;

	/**
	 * Set to true to exit thread
	 */
	private boolean quit = false;

	public TCPThread(App app, Socket socket) {
		System.out.println("New client " + socket.getInetAddress());
		this.app = app;

		this.me = new Friend();
		this.me.setName("clem");
		this.me.setTcpThread(this);

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
				// usually means that the client disconnected improperly
				quit = true;
			} else {
				// do something with the message
				System.out.println("Got raw msg: " + message);
				caseMessage(message);
			}
		}
	}

	public void caseMessage(String message) {
		if (message.startsWith("JOIN")) {
			app.joinMsg(me, Join.fromString(message));
		} else if (message.startsWith("LEAVE")) {
			app.leaveMsg(me, Leave.fromString(message));
		} else if (message.startsWith("LIST")) {
			app.listMsg(me);
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
