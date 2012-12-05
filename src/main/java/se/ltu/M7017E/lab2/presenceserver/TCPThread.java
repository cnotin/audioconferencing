package se.ltu.M7017E.lab2.presenceserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import se.ltu.M7017E.lab2.common.messages.AnswerCall;
import se.ltu.M7017E.lab2.common.messages.Bye;
import se.ltu.M7017E.lab2.common.messages.Call;
import se.ltu.M7017E.lab2.common.messages.Hello;
import se.ltu.M7017E.lab2.common.messages.Join;
import se.ltu.M7017E.lab2.common.messages.Leave;
import se.ltu.M7017E.lab2.common.messages.ListMsg;
import se.ltu.M7017E.lab2.common.messages.StopCall;

public class TCPThread implements Runnable {
	private App app;
	private BufferedReader in;
	private PrintStream out;
	private Client me;

	/**
	 * Set to true to exit thread
	 */
	private boolean quit = false;

	public TCPThread(App app, Socket socket) {
		System.out.println("New client " + socket.getInetAddress());
		this.app = app;

		this.me = new Client(socket.getInetAddress().getHostAddress(), this);

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

				if (message.startsWith("HELLO")) {
					app.msg(me, Hello.fromString(message));
				} else if (message.startsWith("JOIN")) {
					app.msg(me, Join.fromString(message));
				} else if (message.startsWith("LEAVE")) {
					app.msg(me, Leave.fromString(message));
				} else if (message.startsWith("LIST")) {
					app.msg(me, new ListMsg());
				} else if (message.startsWith("BYE")) {
					quit = true; // the msg will be sent after quitting the loop
				} else if (message.startsWith("CALL")) {
					app.msg(Call.fromString(message));

				} else if (message.startsWith("STOPCALL")) {
					app.msg(StopCall.fromString(message));
				} else if (message.startsWith("ANSWERCALL")) {
					AnswerCall answer = AnswerCall.fromString(message);
					if (answer.getAnswer().equals("yes"))
						System.out.println("discussion between "
								+ answer.getSender() + " and "
								+ answer.getReceiver() + " on ports "
								+ answer.getPortReceiver());
					app.msg(answer);
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
}
