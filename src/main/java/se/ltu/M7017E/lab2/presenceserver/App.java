package se.ltu.M7017E.lab2.presenceserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class App {
	@Getter
	private List<Friend> friends = Collections
			.synchronizedList(new LinkedList<Friend>());

	public App() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			try {
				new Thread(new Client(this, serverSocket.accept())).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void broadcast(String message) {
		for (Friend friend : friends) {
			friend.getClient().send(message);
		}
	}
}