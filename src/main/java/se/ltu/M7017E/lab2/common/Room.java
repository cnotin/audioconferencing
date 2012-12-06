package se.ltu.M7017E.lab2.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.Data;
import lombok.NoArgsConstructor;
import se.ltu.M7017E.lab2.presenceserver.Client;

@Data
@NoArgsConstructor
public class Room {
	protected int id;
	private Set<String> audience = new TreeSet<String>();

	public Room(int id) {
		this.id = id;
	}

	public List<String> getAudienceAsStrings() {
		List<String> names = new LinkedList<String>();

		for (String contact : audience) {
			names.add(contact);
		}
		return names;
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

	public String toString() {
		return ("Room " + id);
	}

}
