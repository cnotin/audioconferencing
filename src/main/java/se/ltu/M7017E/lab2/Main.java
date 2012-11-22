package se.ltu.M7017E.lab2;

import java.io.IOException;
import java.net.MulticastSocket;

import javax.swing.SwingUtilities;

import se.ltu.M7017E.lab2.ui.Gui;

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
	
		final App app = new App();
	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui(app);
				gui.setVisible(true);
			}
		});
	}
}
