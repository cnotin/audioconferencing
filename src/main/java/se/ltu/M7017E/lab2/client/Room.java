package se.ltu.M7017E.lab2.client;

import java.util.Set;
import java.util.TreeSet;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Business entity for a Room. A room is a place where people join to talk to
 * everyone at the same time.
 */
@Data
@NoArgsConstructor
public class Room {
	/** Room ID, from 1 to 254 included */
	protected int id;
	/** People who are currently in the room */
	private Set<String> audience = new TreeSet<String>();

	public Room(int id) {
		this.id = id;
	}

	/**
	 * Used to print the room in the UI.
	 * 
	 * @return the string with the room Id, example "Room 42"
	 */
	public String toString() {
		return ("Room " + id);
	}
}
