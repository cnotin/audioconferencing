package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Call {
	private String sender;
	private String receiver;

	public String toString() {
		return "CALL," + sender + "," + receiver;
	}

	public static Call fromString(String str) {
		String[] tokens = str.split(",");

		return new Call(tokens[1], tokens[2]);
	}
}
