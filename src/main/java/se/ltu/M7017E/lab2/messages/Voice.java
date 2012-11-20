package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class Voice {
	private int room;

	public String toString() {
		return "VOICE," + room;
	}

	public static Voice fromString(String str) {
		Voice ret = new Voice();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);

		return ret;
	}
}
