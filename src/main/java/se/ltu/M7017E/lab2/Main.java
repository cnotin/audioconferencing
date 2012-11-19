package se.ltu.M7017E.lab2;

import java.io.IOException;
import java.net.MulticastSocket;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		MulticastSocket socket = null;
		try {
			socket = new MulticastSocket(Preferences.GROUP_PORT);
			socket.joinGroup(new Preferences().GROUP_IP);
			socket.setLoopbackMode(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new UDPServer(socket)).start();
		UDPClient client = new UDPClient(socket);
		client.whosthere();
	}
}
