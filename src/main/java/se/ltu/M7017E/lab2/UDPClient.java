package se.ltu.M7017E.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Sends control messages
 */
public class UDPClient {
	private MulticastSocket socket;

	public UDPClient(MulticastSocket socket) {
		System.out.println("Creating client");
		this.socket = socket;
	}

	public void whosthere() {
		send("WHOSTHERE");
	}

	public void iam(String ip, Long port, String name) {
		send("IAM," + ip + "," + port.toString() + "," + name);
	}

	public void bye(String name) {
		send("BYE," + name);
	}

	public void ping(String name) {
		send("PING," + name);
	}

	/**
	 * Create the packet with the message and send it.
	 * 
	 * @param message
	 *            without any formatting (no '\n' at the end for example)
	 */
	private void send(String message) {
		message += '\n';

		DatagramPacket packet = new DatagramPacket(message.getBytes(),
				message.length(), new Preferences().GROUP_IP,
				Preferences.GROUP_PORT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
