package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

import se.ltu.M7017E.lab2.client.Config;

public class ReceiverPipeline extends Pipeline {
	private static final String RECEIVER_ROOM_PREFIX = "receiver_room";
	private static final String RECEIVER_UNICAST = "receiver_unicast";

	private Map<Integer, RoomReceiver> rooms = new HashMap<Integer, RoomReceiver>();
	private final Element adder = ElementFactory.make("liveadder", null);
	private final Element sink = ElementFactory.make("autoaudiosink", null);
	// THE UnicastReceiver to talk with somebody
	UnicastReceiver unicastReceiver;

	public ReceiverPipeline() {
		super("receiver_pipeline");

		addMany(adder, sink);
		linkMany(adder, sink);
	}

	public void receiveFromRoom(int roomId, long ssrcToIgnore) {
		// don't join if already joined
		if (!rooms.containsKey(roomId)) {
			// create the receiver bin
			RoomReceiver room = new RoomReceiver(RECEIVER_ROOM_PREFIX + roomId,
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
			((RoomReceiver) getElementByName(RECEIVER_ROOM_PREFIX + roomId))
					.getOut();
		}
	}

	public int receiveFromUnicast() {
		// create the receiver bin
		unicastReceiver = new UnicastReceiver(RECEIVER_UNICAST, adder);
		// add it to this
		add(unicastReceiver);
		unicastReceiver.syncStateWithParent();

		return unicastReceiver.getPort();
	}

	public void stopUnicastReceiving() {
		unicastReceiver.getOut();
	}
}
