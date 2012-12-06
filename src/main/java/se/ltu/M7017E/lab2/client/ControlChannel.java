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
import se.ltu.M7017E.lab2.common.messages.Joined;
import se.ltu.M7017E.lab2.common.messages.Left;
import se.ltu.M7017E.lab2.common.messages.StopCall;

/**
 * Manage the control channel
 */
public class ControlChannel implements Runnable {
	@Getter
	private App app;
	@Getter
	private BufferedReader in;
	private PrintStream out;
	private boolean quit = false; // set to true to exit thread
	private int index = 0;
	@Getter
	private List<String> msgList = new ArrayList<String>();
	@Getter
	private String updatedAudience;
	private boolean sendingRoomList = false;

	@Getter
	@Setter
	private Semaphore roomsListFinished = new Semaphore(0);

	public ControlChannel(App app) {
		System.out.println("Creating control channel");
		this.app = app;

		try {
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
			System.out.println("allo");
			app.getGui().acceptACall(message, this.app);
		} else if (message.startsWith("ANSWERCALL")) {
			AnswerCall answer = AnswerCall.fromString(message);
			if (answer.getAnswer().equals("yes")) {
				System.out.println("ip receiver" + answer.getIpReceiver());
				app.getGui().showMessage(
						answer.getReceiver() + " accepted the call");
				app.call(answer.getIpReceiver(), answer.getPortReceiver());
			}
			if (answer.getAnswer().equals("no")) {
				app.getGui().showMessage(
						answer.getReceiver() + " declined the call");
				app.stopCall();
			}

		} else if (message.startsWith("ERROR")) {
			app.getGui().showMessage(message.substring(6, message.length()));
		} else if (message.startsWith("CONNECTEDLIST")) {
			app.setConnected(message);
		} else if (message.startsWith("STOPCALL")) {
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
