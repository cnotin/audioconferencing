package se.ltu.M7017E.lab2.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Joined {
	private int room;
	private String name;

	public String toString() {
		return "JOINED," + room + "," + name;
	}

	/**
	 * build a Joined object from a String. The format of the string must be:
	 * JOINED,<roomName>,<userName>
	 */
	public static Joined fromString(String str) {
		String[] tokens = str.split(",");

		return new Joined(new Integer(tokens[1]), tokens[2]);
	}
}
