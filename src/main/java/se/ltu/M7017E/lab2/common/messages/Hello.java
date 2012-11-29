package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Hello {
	private String name;

	public String toString() {
		return "HELLO," + name;
	}

	public static Hello fromString(String str) {
		Hello ret = new Hello();

		String[] tokens = str.split(",");
		ret.name = tokens[1];

		return ret;
	}
}
