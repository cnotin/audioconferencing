package se.ltu.M7017E.lab2.presenceserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import se.ltu.M7017E.lab2.messages.AnswerCall;
import se.ltu.M7017E.lab2.messages.Bye;
import se.ltu.M7017E.lab2.messages.Call;
import se.ltu.M7017E.lab2.messages.Hello;
import se.ltu.M7017E.lab2.messages.Join;
import se.ltu.M7017E.lab2.messages.Leave;
import se.ltu.M7017E.lab2.messages.ListMsg;
import se.ltu.M7017E.lab2.messages.StopCall;

/**
 * TCP communication thread with a client.
 */
public class TCPThread implements Runnable {
	/** App to report back message */
	private App app;
	/** Input reader */
	private BufferedReader in;
	/** Output printer */
	private PrintStream out;
	private Socket socket;
	/** Client associated to this thread */
	private Client me;

	/** Set to true to exit thread */
	private boolean quit = false;

	public TCPThread(App app, Socket socket) {
		this.app = app;
		this.socket = socket;
		this.me = new Client(this);

		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Got a problem while setting up "
					+ "input/output objects to communicate with client.");
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
				System.err.println("Got a problem while reading "
						+ "a new message from client");
				e.printStackTrace();
			}
			if (message == null) {
				// usually means that the client disconnected improperly
				quit = true;
			} else {
				// do something with the message
				if (message.startsWith("HELLO")) {
					app.msg(me, Hello.fromString(message));
				} else if (message.startsWith("JOIN")) {
					app.msg(me, Join.fromString(message));
				} else if (message.startsWith("LEAVE")) {
					app.msg(me, Leave.fromString(message));
				} else if (message.startsWith("LIST")) {
					app.msg(me, new ListMsg());
				} else if (message.startsWith("BYE")) {
					// the msg will be dealt with after quitting the loop
					quit = true;
				} else if (message.startsWith("CALL")) {
					app.msg(me, Call.fromString(message));
				} else if (message.startsWith("STOPCALL")) {
					app.msg(StopCall.fromString(message));
				} else if (message.startsWith("ANSWERCALL")) {
					app.msg(me, AnswerCall.fromString(message));
				}

			}
		}

		// client quits
		app.msg(me, new Bye());

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

	/**
	 * Get access to peer IP address
	 * 
	 * @return IP address like "12.34.56.78"
	 */
	public String getIp() {
		return socket.getInetAddress().getHostAddress();
	}
}
