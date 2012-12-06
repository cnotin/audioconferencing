package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

import se.ltu.M7017E.lab2.client.Config;

public class ReceiverPipeline extends Pipeline {
	private Map<Integer, RoomReceiver> rooms = new HashMap<Integer, RoomReceiver>();
	private final Element adder = ElementFactory.make("liveadder", null);
	private final Element sink = ElementFactory.make("autoaudiosink", null);

	public ReceiverPipeline() {
		super("receiver");

		addMany(adder, sink);
		linkMany(adder, sink);
	}

	public void receiveFromRoom(int roomId, long ssrcToIgnore) {
		// don't join if already joined
		if (!rooms.containsKey(roomId)) {
			// create the receiver bin
			RoomReceiver room = new RoomReceiver("room" + roomId,
					Config.BASE_IP + roomId, Config.RTP_MULTICAST_PORT,
					ssrcToIgnore);
			rooms.put(roomId, room);
			// add it to this
			add(room);
			room.syncStateWithParent();

			// connect its output to the adder
			room.link(adder);
		}
	}

	public void stopRoomReceiving(int roomId) {
		// can't leave if not joined
		if (rooms.containsKey(roomId)) {
			// TODO
		}
	}

	public int receiveFromUnicast() {
		// create the receiver bin
		UnicastReceiver friend = new UnicastReceiver("receive_unicast", adder);
		// add it to this
		add(friend);
		friend.syncStateWithParent();

		return friend.getPort();
	}

	public void stopUnicastReceiving() {
		((UnicastReceiver) getElementByName("receive_unicast")).getOut();
	}
}
