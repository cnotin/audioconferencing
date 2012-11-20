package se.ltu.M7017E.lab2.messages;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
	private int room;
	private List<String> names = new LinkedList<String>();

	public String toString() {
		StringBuilder sb = new StringBuilder("ROOM,");
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

	public static Room fromString(String str) {
		Room ret = new Room();

		String[] tokens = str.split(",");

		ret.room = new Integer(tokens[1]);
		for (int i = 2; i < tokens.length; i++) {
			ret.names.add(tokens[i]);
		}

		return ret;
	}
}
