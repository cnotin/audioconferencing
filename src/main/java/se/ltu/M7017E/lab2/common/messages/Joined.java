package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Joined {
	private int room;
	private String name;

	public String toString() {
		return "JOINED," + room + "," + name;
	}

	public static Joined fromString(String str) {
		Joined ret = new Joined();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.name = tokens[2];

		return ret;
	}
}
