package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Leave {
	private int room;

	/**
	 * transform the object into a string
	 */
	public String toString() {
		return "LEAVE," + room;
	}

	/**
	 * build a Leave object from a String. The format of the string must be:
	 * "LEAVE",roomName
	 */
	public static Leave fromString(String str) {
		String[] tokens = str.split(",");

		return new Leave(new Integer(tokens[1]));
	}
}
