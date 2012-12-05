package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class AnswerCall {
	private String portReceiver;
	private String sender;
	private String receiver;
	private String answer;
	private String ipReceiver;

	public String toString() {
		return "ANSWERCALL," + portReceiver + "," + sender + "," + receiver
				+ "," + answer + "," + ipReceiver;
	}

	public static AnswerCall fromString(String str) {
		AnswerCall ret = new AnswerCall();
		String[] tokens = str.split(",");
		ret.portReceiver = tokens[1];
		ret.sender = tokens[2];
		ret.receiver = tokens[3];
		ret.answer = tokens[4];
		ret.ipReceiver = tokens[5];

		return ret;
	}
}
