package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class Left {
	private int room;
	private String name;

	public String toString() {
		return "LEFT," + room + "," + name;
	}

	public static Left fromString(String str) {
		Left ret = new Left();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.name = tokens[2];

		return ret;
	}
}
