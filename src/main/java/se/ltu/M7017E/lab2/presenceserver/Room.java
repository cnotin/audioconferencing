package se.ltu.M7017E.lab2.presenceserver;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
	private int id;

	private List<Friend> audience = new LinkedList<Friend>();

	public Room(int id) {
		this.id = id;
	}

	public List<String> getAudienceAsStrings() {
		List<String> names = new LinkedList<String>();

		for (Friend friend : audience) {
			names.add(friend.getName());
		}

		return names;
	}

	public void broadcast(Object message) {
		for (Friend friend : audience) {
			friend.getTcpThread().send(message.toString());
		}
	}
}
