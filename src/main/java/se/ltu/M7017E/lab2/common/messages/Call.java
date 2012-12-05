package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Call {
	private String sender;
	private String receiver;

	public String toString() {
		return "CALL," + sender + "," + receiver;
	}

	public static Call fromString(String str) {
		Call ret = new Call();

		String[] tokens = str.split(",");
		ret.sender = tokens[1];
		ret.receiver = tokens[2];

		return ret;
	}
}
