package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Left {
	private int room;
	private String name;

	/**
	 * transform the object into a string
	 */
	public String toString() {
		return "LEFT," + room + "," + name;
	}

	/**
	 * build a Left object from a String. The format of the string must be:
	 * "LEAVE",roomName,name
	 */
	public static Left fromString(String str) {
		Left ret = new Left();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.name = tokens[2];

		return ret;
	}
}
