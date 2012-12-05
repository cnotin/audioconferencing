package se.ltu.M7017E.lab2.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerCall {
	private int portReceiver;
	private String sender;
	private String receiver;
	private String answer;
	private String ipReceiver;

	public String toString() {
		return "ANSWERCALL," + portReceiver + "," + sender + "," + receiver
				+ "," + answer + "," + ipReceiver;
	}

	public static AnswerCall fromString(String str) {
		String[] tokens = str.split(",");

		return new AnswerCall(new Integer(tokens[1]), tokens[2], tokens[3],
				tokens[4], tokens[5]);
	}
}
