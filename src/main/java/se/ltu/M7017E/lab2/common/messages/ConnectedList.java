package se.ltu.M7017E.lab2.common.messages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ConnectedList {
	@Getter
	private Set<String> connected = new HashSet<String>();

	public String toString() {
		StringBuilder sb = new StringBuilder("CONNECTEDLIST,");

		for (Iterator<String> it = connected.iterator(); it.hasNext();) {
			sb.append(it.next());
			// don't add the separator after the last element
			if (it.hasNext()) {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	public static ConnectedList fromString(String str) {
		ConnectedList ret = new ConnectedList();

		String[] tokens = str.split(",");

		for (int i = 1; i < tokens.length; i++) {
			ret.connected.add(tokens[i]);
		}

		return ret;
	}
}
