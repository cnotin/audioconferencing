package se.ltu.M7017E.lab2.messages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Audience {
	private int room;
	private Set<String> names;

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

	/**
	 * build an Audience object from a String. The format of the string must be:
	 * AUDIENCE,<RoomId>,<Contact1>,<Contact2> ...
	 */
	public static Audience fromString(String str) {
		String[] tokens = str.split(",");

		Set<String> names = new HashSet<String>();
		for (int i = 2; i < tokens.length; i++) {
			names.add(tokens[i]);
		}

		return new Audience(new Integer(tokens[1]), names);
	}
}
