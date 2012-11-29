package se.ltu.M7017E.lab2.presenceserver;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Data;
import se.ltu.M7017E.lab2.common.messages.Joined;
import se.ltu.M7017E.lab2.common.messages.Left;

@Data
public class Room {
	private int id;

	private Set<Client> audience = new HashSet<Client>();

	public Room(int id) {
		this.id = id;
	}

	public List<String> getAudienceAsStrings() {
		List<String> names = new LinkedList<String>();

		for (Client client : audience) {
			names.add(client.getName());
		}

		return names;
	}

	public void broadcast(Object message) {
		for (Client client : audience) {
			client.send(message.toString());
		}
	}

	/**
	 * Remove a client from the room and say it to everyone.
	 * 
	 * @param client
	 *            the client who left
	 */
	public void left(Client client) {
		if (audience.remove(client)) {
			Left left = new Left();
			left.setName(client.getName());
			left.setRoom(id);
			broadcast(left);
		} else {
			System.err.println("Tried to remove " + client + " from room "
					+ this + " but he wasn't found");
		}
	}

	/**
	 * A client joins this room
	 * 
	 * @param client
	 *            the new client in the room
	 */
	public void join(Client client) {
		// announce the newcomer to people in the room
		Joined joined = new Joined();
		joined.setName(client.getName());
		joined.setRoom(id);
		broadcast(joined);

		audience.add(client);
	}

	/**
	 * Says if a client is within the room or not.
	 * 
	 * @param client
	 *            the Client to search in the Room
	 * @return true if present, false if not found
	 */
	public boolean isWithin(Client client) {
		return audience.contains(client);
	}
}
