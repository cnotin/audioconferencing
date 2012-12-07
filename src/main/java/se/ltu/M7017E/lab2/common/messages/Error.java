package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
	/**
	 * Text associated with the error
	 */
	private String text;

	public String toString() {
		return "ERROR," + text;
	}

	public static Error fromString(String str) {
		String[] tokens = str.split(",");

		return new Error(tokens[1]);
	}
}
