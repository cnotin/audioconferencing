package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class Leave {
	private int room;

	public String toString() {
		return "LEAVE," + room;
	}

	public static Leave fromString(String str) {
		Leave ret = new Leave();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);

		return ret;
	}
}
