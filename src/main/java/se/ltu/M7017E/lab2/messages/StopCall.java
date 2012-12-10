package se.ltu.M7017E.lab2.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StopCall {
	public String receiver;

	public String toString() {
		return "STOPCALL," + receiver;
	}

	/**
	 * build a Left object from a String. The format of the string must be:
	 * STOPCALL,<receiverName>
	 */
	public static StopCall fromString(String message) {
		String[] tokens = message.split(",");

		return new StopCall(tokens[1]);
	}
}
