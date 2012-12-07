package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

import se.ltu.M7017E.lab2.client.Config;

/**
 * GStreamer pipeline for the receiving part. Can manage several multicast (for
 * rooms) and one unicast channel at the same time.
 */
public class ReceiverPipeline extends Pipeline {
	/** Prefix to name the rooms bins */
	private static final String RECEIVER_ROOM_PREFIX = "receiver_room";

	// TODO: remove
	private Map<Integer, RoomReceiver> rooms = new HashMap<Integer, RoomReceiver>();

	private final Element adder = ElementFactory.make("liveadder", null);
	private final Element sink = ElementFactory.make("autoaudiosink", null);
	// THE UnicastReceiver to talk with someone
	UnicastReceiver unicastReceiver = null;

	public ReceiverPipeline() {
		super("receiver_pipeline");

		addMany(adder, sink);
		linkMany(adder, sink);
	}

	/**
	 * Create the audio stuff to receive from a room.
	 * 
	 * @param roomId
	 *            room number
	 * @param ssrcToIgnore
	 *            My SSRC to ignore from the received stream, avoid echo!
	 */
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

	/**
	 * Cleanly remove the audio stuff which were used to receive from a room.
	 * 
	 * @param roomId
	 *            the room we were connected to
	 */
	public void stopRoomReceiving(int roomId) {
		// can't leave if not joined
		if (rooms.containsKey(roomId)) {
			((RoomReceiver) getElementByName(RECEIVER_ROOM_PREFIX + roomId))
					.getOut();
		}
	}

	/**
	 * Setup the audio stuff when initiating a call and prepare for the incoming
	 * stream.
	 * 
	 * @return port number that has been opened to receive the incoming stream
	 *         from friend
	 */
	public int receiveFromUnicast() {
		// create the receiver bin
		unicastReceiver = new UnicastReceiver(adder);
		// add it to this
		add(unicastReceiver);
		unicastReceiver.syncStateWithParent();

		return unicastReceiver.getPort();
	}

	/**
	 * Stop receiving from my friend.
	 */
	public void stopUnicastReceiving() {
		if (unicastReceiver != null) {
			unicastReceiver.getOut();
		}
		unicastReceiver = null;
	}
}
