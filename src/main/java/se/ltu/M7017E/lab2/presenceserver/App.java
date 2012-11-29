package se.ltu.M7017E.lab2.presenceserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import se.ltu.M7017E.lab2.common.messages.AnswerCall;
import se.ltu.M7017E.lab2.common.messages.Audience;
import se.ltu.M7017E.lab2.common.messages.Bye;
import se.ltu.M7017E.lab2.common.messages.Call;
import se.ltu.M7017E.lab2.common.messages.Hello;
import se.ltu.M7017E.lab2.common.messages.Join;
import se.ltu.M7017E.lab2.common.messages.Leave;
import se.ltu.M7017E.lab2.common.messages.ListMsg;
import se.ltu.M7017E.lab2.common.messages.RoomsStart;
import se.ltu.M7017E.lab2.common.messages.RoomsStop;

public class App {
	@Getter
	private Set<Client> clients = new HashSet<Client>();
	private Map<Integer, Room> rooms = new HashMap<Integer, Room>();

	public App() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			try {
				new Thread(new TCPThread(this, serverSocket.accept())).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void msg(Client client, Hello hello) {
		client.setName(hello.getName());
		clients.add(client);
	}

	public void msg(Client client, Bye bye) {
		// someone leaves, maybe he forgot to leave properly each room => do it
		for (Map.Entry<Integer, Room> iter : rooms.entrySet()) {
			Room room = iter.getValue();
			if (room.isWithin(client)) {
				room.left(client);
			}
		}

		// removes him from the known clients in the server
		clients.remove(client);
	}

	public void msg(Client client, Join join) {
		int roomId = join.getRoom();

		Room room = rooms.get(roomId);
		if (room == null) {
			// first person to enter the room => create it
			room = new Room(roomId);
			this.rooms.put(roomId, room);
		}

		room.join(client);

		// tell to the newcomer who's there
		Audience audience = new Audience();
		audience.setRoom(roomId);
		audience.setNames(room.getAudienceAsStrings());
		client.send(audience);
	}

	public void msg(Call call) {
		Client client = findClientsByName(call.getReceiver());
		System.out.println("sending message to" + client.getName());
		client.send(call.toString());
	}

	public void msg(AnswerCall answer) {
		Client client = findClientsByName(answer.getSender());
		System.out.println("sending message to" + client.getName());
		client.send(answer.toString());
	}

	public void msg(Client client, Leave leave) {
		int roomId = leave.getRoom();
		Room room = this.rooms.get(roomId);

		// TODO Stop player

		room.left(client);
	}

	public void msg(Client client, ListMsg list) {
		client.send(new RoomsStart());

		// for each room (key = id, value = room)
		for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
			Audience audience = new Audience();
			audience.setRoom(entry.getKey());
			audience.setNames(entry.getValue().getAudienceAsStrings());

			client.send(audience);
		}

		client.send(new RoomsStop());
	}

	public void broadcast(String message) {
		for (Client client : clients) {
			client.send(message);
		}
	}

	public Client findClientsByName(String name) {
		System.out.println("name to search " + name + "<");
		Client clientToReturn = null;
		System.out.println("number of clients" + clients.size());
		for (Client client : clients) {
			System.out.println("client name : " + client.getName());
			if (client.getName().equals(name)) {
				clientToReturn = client;
			}
		}
		return clientToReturn;
	}
}