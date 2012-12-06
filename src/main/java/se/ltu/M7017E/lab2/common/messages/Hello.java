package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hello {
	private String name;

	/**
	 * transform the object into a string
	 */
	public String toString() {
		return "HELLO," + name;
	}

	/**
	 * build a Hello object from a String. The format of the string must be:
	 * "HELLO",userName
	 */
	public static Hello fromString(String str) {
		String[] tokens = str.split(",");

		return new Hello(tokens[1]);
	}
}
