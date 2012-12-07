package se.ltu.M7017E.lab2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import lombok.Getter;
import lombok.Setter;
import se.ltu.M7017E.lab2.common.messages.AnswerCall;
import se.ltu.M7017E.lab2.common.messages.Error;
import se.ltu.M7017E.lab2.common.messages.Joined;
import se.ltu.M7017E.lab2.common.messages.Left;
import se.ltu.M7017E.lab2.common.messages.StopCall;

/**
 * Manage the control channel with the central server. Its job is to store who's
 * connected, what are the existing rooms, who's in which room, and to message
 * the users when someone joins or leave a room. The server also handle unicast
 * calls negotiation (UDP ports and approval/refusal by callee).
 */
public class ControlChannel implements Runnable {
	@Getter
	private App app;
	private BufferedReader in;
	private PrintStream out;
	private boolean quit = false; // set to true to exit thread
	private int index = 0;
	@Getter
	private List<String> msgList = new ArrayList<String>();
	@Getter
	private String updatedAudience;
	private boolean sendingRoomList = false;

	/**
	 * Semaphore to signal that this thread has finished to receive a list of
	 * rooms from the server.
	 */
	@Getter
	@Setter
	private Semaphore roomsListFinished = new Semaphore(0);

	public ControlChannel(App app) {
		this.app = app;
		System.out.println("Creating control channel");

		try {
			// connect to server
			Socket socket = new Socket(InetAddress.getByName("localhost"), 4000);
			// Socket socket = new
			// Socket(InetAddress.getByName("130.240.53.166"),
			// 4000);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Read incoming messages in a loop */
	@Override
	public void run() {
		String message = null;
		while (!quit) {
			try {
				message = in.readLine();
			} catch (IOException e) {
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

	/**
	 * Apply the method needed depending on the message received from the server
	 * 
	 * @param message
	 *            the message received from the server
	 */
	public void caseMessage(String message) {
		if (message.startsWith("JOINED")) {
			// someone joins a room
			app.msg(Joined.fromString(message));
		} else if (message.startsWith("LEFT")) {
			// someone left a room
			app.msg(Left.fromString(message));
		} else if (message.startsWith("ROOMS_START")) {
			msgList.clear();
			sendingRoomList = true;
		} else if (message.startsWith("AUDIENCE")) {
			if (sendingRoomList) {
				// Part of a list
				msgList.add(index, message);
				index++;
			} else {
				// Someone just joined the room
				updatedAudience = message;
				this.getRoomsListFinished().release();
			}
		} else if (message.startsWith("ROOMS_STOP")) {
			index = 0;
			sendingRoomList = false;
			// release the semaphore so the APP can continue
			this.roomsListFinished.release();
		} else if (message.startsWith("CALL")) {
			// someone called me
			System.out.println("allo");
			app.getGui().acceptACall(message, this.app);
		} else if (message.startsWith("ANSWERCALL")) {
			// I received someone's answer about my call
			AnswerCall answer = AnswerCall.fromString(message);
			if (answer.getAnswer().equals("yes")) {
				// he accepted
				System.out.println("ip receiver" + answer.getIpReceiver());
				app.getGui().showMessage(
						answer.getReceiver() + " accepted the call");
				// launch the streaming to him
				app.call(answer.getIpReceiver(), answer.getPortReceiver());
			}
			if (answer.getAnswer().equals("no")) {
				app.getGui().showMessage(
						answer.getReceiver() + " declined the call");
				/*
				 * when asking for a call, one already open a receiving port and
				 * send its number to callee. If the callee refuses we must
				 * cancel this
				 */
				app.stopCall();
			}

		} else if (message.startsWith("ERROR")) {
			// server sent us a textual error message
			app.getGui().showMessage(Error.fromString(message).getText());
		} else if (message.startsWith("CONNECTEDLIST")) {
			// list of people connected to server
			app.setConnected(message);
		} else if (message.startsWith("STOPCALL")) {
			// my friend asks to stop the unicast call
			app.msg(StopCall.fromString(message));
		}
	}

	/**
	 * Send the message.
	 * 
	 * @param message
	 *            without any formatting (no '\n' at the end for example)
	 */
	public void send(String message) {
		System.out.println("Client send: " + message);
		out.println(message);
	}
}
