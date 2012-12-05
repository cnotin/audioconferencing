package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.BaseSrc;

import se.ltu.M7017E.lab2.client.Config;
import se.ltu.M7017E.lab2.client.Tool;

public class SenderPipeline extends Pipeline {
	private Map<Integer, SenderBin> rooms = new HashMap<Integer, SenderBin>();
	private final BaseSrc src = (BaseSrc) ElementFactory.make("alsasrc", null);
	private final Element tee = ElementFactory.make("tee", null);

	public SenderPipeline() {
		super("sender");

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
	 * @return the RTP SSRC of this sender, or (-1) if the room was already
	 *         joined
	 */
	public long streamTo(int roomId) {
		// don't join if already joined
		if (!rooms.containsKey(roomId)) {
			if (isPlaying()) {
				pause();
			}

			// create the sender bin
			SenderBin room = new SenderBin("sender_room" + roomId,
					Config.BASE_IP + roomId, Config.RTP_MULTICAST_PORT, true);
			rooms.put(roomId, room);
			// add it to this
			add(room);

			// connect its input to the tee
			Tool.successOrDie("tee-roomSender", tee.getRequestPad("src%d")
					.link(room.getStaticPad("sink")).equals(PadLinkReturn.OK));

			play();

			return room.getSSRC();
		}
		return -1;
	}

	public void stopStreamingToRoom(int roomId) {
		// can't leave if not joined
		if (rooms.containsKey(roomId)) {
			// TODO
		}
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
		if (isPlaying()) {
			pause();
		}

		// create the sender bin
		SenderBin friend = new SenderBin("send_unicast", ip, port, false);
		// add it to this
		add(friend);

		// connect its input to the tee
		Tool.successOrDie("tee-unicastSender",
				tee.getRequestPad("src%d").link(friend.getStaticPad("sink"))
						.equals(PadLinkReturn.OK));

		play();
	}

	public void stopStreamingToUnicast() {
		if (isPlaying()) {
			pause();
		}

		((SenderBin) getElementByName("send_unicast")).getOut();

		play();
	}
}
