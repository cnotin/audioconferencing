package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Join {
	private int room;

	public String toString() {
		return "JOIN," + room;
	}

	public static Join fromString(String str) {
		Join ret = new Join();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);

		return ret;
	}
}
