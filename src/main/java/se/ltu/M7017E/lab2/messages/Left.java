package se.ltu.M7017E.lab2.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Left {
	private int room;
	private String name;

	public String toString() {
		return "LEFT," + room + "," + name;
	}

	/**
	 * build a Left object from a String. The format of the string must be:
	 * LEAVE,<roomName>,<name>
	 */
	public static Left fromString(String str) {
		String[] tokens = str.split(",");

		return new Left(new Integer(tokens[1]), tokens[2]);
	}
}
