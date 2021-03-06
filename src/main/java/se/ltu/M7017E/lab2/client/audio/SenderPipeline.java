package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.BaseSrc;

import se.ltu.M7017E.lab2.client.Config;
import se.ltu.M7017E.lab2.client.Tool;

/**
 * GStreamer pipeline for the sending part. Can manage several multicast (for
 * rooms) and one unicast channel at the same time.
 */
public class SenderPipeline extends Pipeline {
	/** Name _the_ unicast sender bin */
	private static final String SENDER_UNICAST = "sender_unicast";
	/** Prefix to name the rooms bins */
	private static final String SENDER_ROOM_PREFIX = "sender_room";

	private final BaseSrc src = (BaseSrc) ElementFactory.make("alsasrc", null);
	private final Element tee = ElementFactory.make("tee", null);
	// THE SenderBin to talk with somebody
	SenderBin unicastSender = null;

	public SenderPipeline() {
		super("sender_pipeline");

		// live source => drop stream when in paused state
		src.setLive(true);

		addMany(src, tee);
		Tool.successOrDie("src-tee", linkMany(src, tee));
	}

	/**
	 * Start streaming to a room (multicast) thus adding all necessary stuff to
	 * this pipeline. At the end the streaming of the sound to it will be
	 * automatically started.
	 * 
	 * @param roomId
	 *            id of the room to join
	 * @return the RTP SSRC of this sender
	 */
	public long streamTo(int roomId) {
		// create the sender bin, name it after the room id
		SenderBin room = new SenderBin(SENDER_ROOM_PREFIX + roomId,
				Config.BASE_IP + roomId, Config.RTP_MULTICAST_PORT, true);
		// add it to this
		add(room);
		room.syncStateWithParent();

		// connect its input to the tee
		Tool.successOrDie("tee-roomSender",
				tee.getRequestPad("src%d").link(room.getStaticPad("sink"))
						.equals(PadLinkReturn.OK));

		play();

		return room.getSSRC();
	}

	/**
	 * Stop sending sound to a room and remove audio stuff that were created
	 * 
	 * @param roomId
	 *            Room ID
	 */
	public void stopStreamingToRoom(int roomId) {
		((SenderBin) getElementByName(SENDER_ROOM_PREFIX + roomId)).getOut();
	}

	/**
	 * Stream to one contact (unicast) thus adding all necessary stuff to this
	 * pipeline. At the end the streaming of the sound to him will be
	 * automatically started.
	 * 
	 * @param ip
	 *            IP adress of the person to join
	 * @param port
	 *            the port on which he listens
	 */
	public void streamTo(String ip, int port) {
		// create the sender bin
		unicastSender = new SenderBin(SENDER_UNICAST, ip, port, false);
		// add it to this
		add(unicastSender);
		unicastSender.syncStateWithParent();

		// connect its input to the tee
		Tool.successOrDie(
				"tee-unicastSender",
				tee.getRequestPad("src%d")
						.link(unicastSender.getStaticPad("sink"))
						.equals(PadLinkReturn.OK));

		play();
	}

	/**
	 * Stop streaming to someone and remove useless audio stuff that were
	 * created.
	 */
	public void stopStreamingToUnicast() {
		if (unicastSender != null) {
			unicastSender.getOut();
		}
		unicastSender = null;
	}
}
