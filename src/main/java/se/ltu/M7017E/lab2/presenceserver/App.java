package se.ltu.M7017E.lab2.presenceserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import se.ltu.M7017E.lab2.messages.Audience;
import se.ltu.M7017E.lab2.messages.Join;
import se.ltu.M7017E.lab2.messages.Joined;
import se.ltu.M7017E.lab2.messages.Leave;
import se.ltu.M7017E.lab2.messages.Left;
import se.ltu.M7017E.lab2.messages.RoomsStart;
import se.ltu.M7017E.lab2.messages.RoomsStop;
import se.ltu.M7017E.lab2.messages.Speakers;
import se.ltu.M7017E.lab2.messages.Voice;
import se.ltu.M7017E.lab2.messages.VoiceNOk;
import se.ltu.M7017E.lab2.messages.VoiceOk;

public class App {
	@Getter
	private List<Friend> friends = Collections
			.synchronizedList(new LinkedList<Friend>());
	private Map<Integer, Room> rooms = Collections
			.synchronizedMap(new HashMap<Integer, Room>());

	public App() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			try {
				new Thread(new TCPThread(this, serverSocket.accept())).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void joinMsg(Friend friend, Join join) {
		int roomId = join.getRoom();

		Room room = rooms.get(roomId);
		if (room == null) {
			// first person to enter the room => create it
			room = new Room(roomId);
			this.rooms.put(roomId, room);
		}
		room.getAudience().add(friend);

		// announce the newcomer to people in the room
		Joined joined = new Joined();
		joined.setName(friend.getName());
		joined.setRoom(roomId);
		room.broadcast(joined);

		// tell to the newcomer who's there
		Audience audience = new Audience();
		audience.setRoom(roomId);
		audience.setNames(room.getAudienceAsStrings());
		friend.getTcpThread().send(audience.toString());

		// tell to the newcomer who are the speakers
		Speakers speakers = new Speakers();
		Map<Integer, String> speakersMap = new HashMap<Integer, String>();
		for (int i = 0; i < room.getSpeakers().length; i++) {
			if (room.getSpeakers()[i] != null) {
				speakersMap.put(i, room.getSpeakers()[i].getName());
			}
		}
		speakers.setPeople(speakersMap);
		friend.getTcpThread().send(speakers.toString());
	}

	public void leaveMsg(Friend friend, Leave leave) {
		int roomId = leave.getRoom();
		Room room = this.rooms.get(roomId);

		// TODO Stop player
		// TODO recycle the port if he was a speaker
		// TODO really remove from the room

		// tell to everyone that he's gone
		Left left = new Left();
		left.setName(friend.getName());
		left.setRoom(roomId);
		room.broadcast(left);
	}

	public void listMsg(Friend friend) {
		friend.send(new RoomsStart());

		// for each room (key = id, value = room)
		for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
			Audience audience = new Audience();
			audience.setRoom(entry.getKey());
			audience.setNames(entry.getValue().getAudienceAsStrings());

			friend.send(audience);
		}

		friend.send(new RoomsStop());
	}

	public void voiceMsg(Friend friend, Voice voice) {
		int roomId = voice.getRoom();
		Room room = this.rooms.get(roomId);

		int port = room.getFreeSpeakerSlot(friend);
		if (port == -1) {
			// room full => refuses
			VoiceNOk voiceNOk = new VoiceNOk();
			voiceNOk.setRoom(roomId);
			friend.send(voiceNOk);
		} else {
			// accept the client
			VoiceOk voiceOk = new VoiceOk();
			voiceOk.setRoom(roomId);
			voiceOk.setPort(port);
			friend.send(voiceOk);
		}
	}

	public void broadcast(String message) {
		for (Friend friend : friends) {
			friend.send(message);
		}
	}

}