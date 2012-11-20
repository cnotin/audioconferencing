package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class VoiceOk {
	private int room;
	private int port;

	public String toString() {
		return "VOICE_OK," + room + "," + port;
	}

	public static VoiceOk fromString(String str) {
		VoiceOk ret = new VoiceOk();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);
		ret.port = new Integer(tokens[2]);

		return ret;
	}
}
