package se.ltu.M7017E.lab2.presenceserver;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import se.ltu.M7017E.lab2.messages.Joined;
import se.ltu.M7017E.lab2.messages.Left;

/**
 * Business entity for a Room. A room is a place where people join to talk to
 * everyone at the same time.
 */
@Data
public class Room {
	/** Room ID, from 1 to 254 included */
	private int id;
	/** People who are currently in the room */
	private Set<Client> audience = new HashSet<Client>();

	public Room(int id) {
		this.id = id;
	}

	/**
	 * To easily get room's audience as a list of Strings (Clients' names)
	 * 
	 * @return a List of Clients' names
	 */
	public Set<String> getAudienceAsStrings() {
		Set<String> names = new HashSet<String>();

		for (Client client : audience) {
			names.add(client.getName());
		}

		return names;
	}

	/**
	 * Send a message to everyone in the room.
	 * 
	 * @param message
	 *            any Object with a valid toString method which will be
	 *            automatically called
	 */
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
			broadcast(new Left(id, client.getName()));
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
		broadcast(new Joined(id, client.getName()));

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
