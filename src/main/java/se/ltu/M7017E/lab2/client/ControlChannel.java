package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import se.ltu.M7017E.lab2.client.ui.AcceptACallDialog;
import se.ltu.M7017E.lab2.common.messages.AnswerCall;
import se.ltu.M7017E.lab2.common.messages.Call;

/**
 * Manage the control channel
 */
public class ControlChannel implements Runnable {
	private App app;
	private BufferedReader in;
	private PrintStream out;
	private boolean quit = false; // set to true to exit thread

	public ControlChannel(App app) {
		System.out.println("Creating control channel");
		this.app = app;

		try {
			Socket socket = new Socket(InetAddress.getByName("localhost"), 4000);
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
		if (message.startsWith("JOINED")) {

		} else if (message.startsWith("LEFT")) {

		} else if (message.startsWith("ROOMS_START")) {

		} else if (message.startsWith("ROOM")) {

		} else if (message.startsWith("ROOMS_STOP")) {

		} else if (message.startsWith("CALL")) {
			Call call = Call.fromString(message);
			new AcceptACallDialog(this, call);
		} else if (message.startsWith("ANSWERCALL")) {
			AnswerCall answer = AnswerCall.fromString(message);
			if (answer.getAnswer().equals("yes"))
				System.out.println(answer.getReceiver() + " accepted the call");
			if (answer.getAnswer().equals("no"))
				System.out.println(answer.getReceiver() + " declined the call");
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
