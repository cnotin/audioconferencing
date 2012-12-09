package se.ltu.M7017E.lab2.common.messages;

import lombok.Getter;
import lombok.Setter;

public class StopCall {
	@Getter
	@Setter
	public String receiver;

	public String toString() {
		return "STOPCALL," + receiver;
	}

	/**
	 * build a Left object from a String. The format of the string must be:
	 * STOPCALL,<receiverName>
	 */
	public static StopCall fromString(String message) {
		StopCall ret = new StopCall();
		String[] tokens = message.split(",");
		ret.receiver = tokens[1];
		return ret;
	}
}
