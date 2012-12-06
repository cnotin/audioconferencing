package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Joined {
	private int room;
	private String name;

	/**
	 * transform the object into a string
	 */
	public String toString() {
		return "JOINED," + room + "," + name;
	}

	/**
	 * build a Joined object from a String. The format of the string must be:
	 * "JOINED",roomName,userName
	 */
	public static Joined fromString(String str) {
		Joined ret = new Joined();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.name = tokens[2];

		return ret;
	}
}
