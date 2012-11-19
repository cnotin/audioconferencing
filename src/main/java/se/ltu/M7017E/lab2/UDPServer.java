package se.ltu.M7017E.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Receives control messages.
 */
public class UDPServer implements Runnable {
	private byte[] buf = new byte[Preferences.BUFSIZE];
	private MulticastSocket socket;

	public UDPServer(MulticastSocket socket) {
		System.out.println("Creating server");
		this.socket = socket;
	}

	@Override
	public void run() {
		while (true) {
			DatagramPacket packet = new DatagramPacket(buf, Preferences.BUFSIZE);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int len = packet.getLength() - 1;
			String message = new String(packet.getData(), 0, len);
			System.out.println(len + "-" + message);
			caseMessage(message);
		}
	}

	private void caseMessage(String message) {
		if (message.startsWith("WHOSTHERE")) {

		} else if (message.startsWith("IAM")) {

		} else if (message.startsWith("BYE")) {

		} else if (message.startsWith("PING")) {

		} else {
			System.out.println("Unknown message !");
		}
	}
}
