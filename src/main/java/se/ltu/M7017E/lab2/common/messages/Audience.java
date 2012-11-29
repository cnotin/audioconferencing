package se.ltu.M7017E.lab2.common.messages;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class Audience {
	private int room;
	private List<String> names = new LinkedList<String>();

	public String toString() {
		StringBuilder sb = new StringBuilder("AUDIENCE,");
		sb.append(room);
		sb.append(',');

		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			sb.append(it.next());
			// don't add the separator after the last element
			if (it.hasNext()) {
				sb.append(',');
			}
		}

		return sb.toString();
	}

	public static Audience fromString(String str) {
		Audience ret = new Audience();

		String[] tokens = str.split(",");

		ret.room = new Integer(tokens[1]);
		for (int i = 2; i < tokens.length; i++) {
			ret.names.add(tokens[i]);
		}

		return ret;
	}
}
