package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Join {
	private int room;

	/**
	 * transform the object into a string
	 */
	public String toString() {
		return "JOIN," + room;
	}

	/**
	 * build a Hello object from a String. The format of the string must be:
	 * "JOIN",room
	 */
	public static Join fromString(String str) {
		Join ret = new Join();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);

		return ret;
	}
}
