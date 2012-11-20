package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class Speaker {
	private int room;
	private String name;
	private int port;

	public String toString() {
		return "SPEAKER," + room + "," + name + "," + port;
	}

	public static Speaker fromString(String str) {
		Speaker ret = new Speaker();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.name = tokens[2];
		ret.port = new Integer(tokens[3]);

		return ret;
	}
}
