package se.ltu.M7017E.lab2.common.messages;

import lombok.Data;

@Data
public class AnswerCall {
	private String portSender;
	private String portReceiver;
	private String sender;
	private String receiver;
	private String answer;

	public String toString() {
		return "ANSWERCALL," + portSender + "," + portReceiver + "," + sender
				+ "," + receiver + "," + answer;
	}

	public static AnswerCall fromString(String str) {
		AnswerCall ret = new AnswerCall();

		String[] tokens = str.split(",");
		ret.portSender = tokens[1];
		ret.portReceiver = tokens[2];
		ret.sender = tokens[3];
		ret.receiver = tokens[4];
		ret.answer = tokens[5];

		return ret;
	}
}
