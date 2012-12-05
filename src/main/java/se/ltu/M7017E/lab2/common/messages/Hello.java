package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hello {
	private String name;

	public String toString() {
		return "HELLO," + name;
	}

	public static Hello fromString(String str) {
		String[] tokens = str.split(",");

		return new Hello(tokens[1]);
	}
}
