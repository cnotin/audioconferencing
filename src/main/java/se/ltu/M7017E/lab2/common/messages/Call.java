package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class Call {
	private String sender;
	private String receiver;
	private String port;

	public String toString() {
		return "CALL," + port + "," + sender + "," + receiver;
	}

	public static Call fromString(String str) {
		Call ret = new Call();

		String[] tokens = str.split(",");
		ret.port = tokens[1];
		ret.sender = tokens[2];
		ret.receiver = tokens[3];

		System.out.println(ret.sender + " is calling " + ret.receiver
				+ " on port " + ret.port);
		return ret;
	}
}
