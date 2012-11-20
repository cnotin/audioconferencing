package se.ltu.M7017E.lab2.messages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.Data;

@Data
public class ThereAre {
	private int room;
	// key=port, value=name
	private Map<Integer, String> people = new HashMap<Integer, String>();

	public String toString() {
		StringBuilder sb = new StringBuilder("THERE_ARE,");
		sb.append(room);
		sb.append(',');

		for (Iterator<Integer> it = people.keySet().iterator(); it.hasNext();) {
			Integer currKey = it.next();
			sb.append(currKey);
			sb.append(',');
			sb.append(people.get(currKey));

			// don't add the separator after the last element
			if (it.hasNext()) {
				sb.append(',');
			}
		}

		return sb.toString();
	}

	public static ThereAre fromString(String str) {
		ThereAre ret = new ThereAre();

		String[] tokens = str.split(",");

		ret.room = new Integer(tokens[1]);

		for (int i = 2; i < tokens.length; i += 2) {
			ret.people.put(new Integer(tokens[i]), tokens[i + 1]);
		}

		return ret;
	}
}
