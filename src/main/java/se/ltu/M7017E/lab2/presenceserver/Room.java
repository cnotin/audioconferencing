package se.ltu.M7017E.lab2.presenceserver;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
	private int id;

	private List<Friend> audience = new LinkedList<Friend>();
	private Friend[] speakers = new Friend[254];

	public Room(int id) {
		this.id = id;
	}

	/**
	 * TODO
	 * 
	 * @param friend
	 *            TODO
	 * @return (-1) if no free slot anymore (room is full)
	 */
	public int getFreeSpeakerSlot(Friend friend) {
		int free = -1;

		for (int i = 0; i < speakers.length && free == -1; i++) {
			if (speakers[i] == null) {
				free = i;
			}
		}

		if (free != -1) {
			// found one => book it
			speakers[free] = friend;
		}

		return free;
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
