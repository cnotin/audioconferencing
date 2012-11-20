package se.ltu.M7017E.lab2.messages;

import lombok.Data;

@Data
public class VoiceNOk {
	private int room;

	public String toString() {
		return "VOICE_NOK," + room;
	}

	public static VoiceNOk fromString(String str) {
		VoiceNOk ret = new VoiceNOk();

		String[] tokens = str.split(",");
		ret.room = new Integer(tokens[1]);

		return ret;
	}
}
