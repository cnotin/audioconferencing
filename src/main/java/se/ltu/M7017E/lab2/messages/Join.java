package se.ltu.M7017E.lab2.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Join {
	private int room;

	public String toString() {
		return "JOIN," + room;
	}

	/**
	 * build a Hello object from a String. The format of the string must be:
	 * JOIN,<room>
	 */
	public static Join fromString(String str) {
		String[] tokens = str.split(",");

		return new Join(new Integer(tokens[1]));
	}
}
