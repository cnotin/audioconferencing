package se.ltu.M7017E.lab2.presenceserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;
import se.ltu.M7017E.lab2.messages.AnswerCall;
import se.ltu.M7017E.lab2.messages.Audience;
import se.ltu.M7017E.lab2.messages.Bye;
import se.ltu.M7017E.lab2.messages.Call;
import se.ltu.M7017E.lab2.messages.ConnectedList;
import se.ltu.M7017E.lab2.messages.Hello;
import se.ltu.M7017E.lab2.messages.Join;
import se.ltu.M7017E.lab2.messages.Leave;
import se.ltu.M7017E.lab2.messages.ListMsg;
import se.ltu.M7017E.lab2.messages.RoomsStart;
import se.ltu.M7017E.lab2.messages.RoomsStop;
import se.ltu.M7017E.lab2.messages.StopCall;

/**
 * Main class for presence server.
 */
/**
 * @author Clem
 * 
 */
public class App {
	/** connected clients */
	@Getter
	private Set<Client> clients = new HashSet<Client>();
	/** currently opened rooms, sorted by id */
	private Map<Integer, Room> rooms = new TreeMap<Integer, Room>();

	public App() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4000);
		} catch (IOException e) {
			System.err.println("Got a problem while trying to "
					+ "open server port");
			e.printStackTrace();
		}

		while (true) {
			try {
				// create one thread per new client
				new Thread(new TCPThread(this, serverSocket.accept())).start();
			} catch (IOException e) {
				System.err.println("Got a problem while trying to "
						+ "create thread for a client");
				e.printStackTrace();
			}
		}
	}

	private Set<String> getClientsAsStrings() {
		Set<String> nameClients = new HashSet<String>();
		for (Client user : clients) {
			nameClients.add(user.name);
		}
		return nameClients;
	}

	/**
	 * Refresh the list of clients connected when a Hello message is received,
	 * and send a list of the connected names to each connected client with a
	 * ConnectedList message
	 * 
	 * @param client
	 * @param hello
	 */
	public void msg(Client client, Hello hello) {
		client.setName(hello.getName());
		clients.add(client);

		broadcastUpdatedListOfConnectedClient();
	}

	/**
	 * Broadcast the updated list of connected clients. Useful when someone
	 * enters/quits the server.
	 */
	private void broadcastUpdatedListOfConnectedClient() {
		broadcast(new ConnectedList(getClientsAsStrings()).toString());
	}

	/**
	 * Refresh the list of clients connected when a Bye message is received, and
	 * send a list of the connected names to each connected client with a
	 * ConnectedList message
	 * 
	 * @param client
	 * @param bye
	 */
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

		broadcastUpdatedListOfConnectedClient();
	}

	/**
	 * Someone joined a room
	 * 
	 * @param client
	 * @param join
	 */
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
		client.send(new Audience(roomId, room.getAudienceAsStrings()));
	}

	/**
	 * Check if the receiver of the call is connected. If he is the call message
	 * is transmitted to the receiver, else an ERROR message is sent to the
	 * sender
	 * 
	 * @param sender
	 * @param call
	 */
	public void msg(Client sender, Call call) {
		Client receiver = findClientByName(call.getReceiver());
		if (receiver != null) {
			call.setIpSender(sender.getIp());
			receiver.send(call.toString());
		} else {
			sender = findClientByName(call.getSender());
			sender.send(new Error(call.getReceiver() + " is not connected :("));
		}
	}

	/**
	 * Send a message to tell the receiver that the call is finished
	 * 
	 * @param stop
	 */
	public void msg(StopCall stop) {
		Client client = findClientByName(stop.getReceiver());
		client.send(stop.toString());
	}

	/**
	 * Send the AnswerCall message (information about the answerer + the answer
	 * yes/no) to the requester of a call
	 * 
	 */
	public void msg(Client answerer, AnswerCall answer) {
		if (answer.getAnswer().equals("yes")) {
		}
		Client requester = findClientByName(answer.getSender());
		answer.setIpReceiver(answerer.getIp());
		requester.send(answer.toString());
	}

	/**
	 * Someone left a room
	 * 
	 * @param client
	 * @param leave
	 */
	public void msg(Client client, Leave leave) {
		// removes him properly from the room
		this.rooms.get(leave.getRoom()).left(client);
	}

	/**
	 * Someone asked for a list of rooms
	 * 
	 * @param client
	 * @param list
	 */
	public void msg(Client client, ListMsg list) {
		client.send(new RoomsStart());

		// for each room (key = id, value = room)
		for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
			client.send(new Audience(entry.getKey(), entry.getValue()
					.getAudienceAsStrings()));
		}

		client.send(new RoomsStop());
	}

	/**
	 * Send a message to all the connected clients
	 */
	public void broadcast(String message) {
		for (Client client : clients) {
			client.send(message);
		}
	}

	/**
	 * Find the client who uses this 'name'.
	 * 
	 * @param name
	 *            the Client's name to search for
	 * @return the Client, or null if not found
	 */
	public Client findClientByName(String name) {
		for (Client client : clients) {
			if (client.getName().equals(name)) {
				return client;
			}
		}
		return null;
	}

}