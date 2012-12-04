package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Bin;
import org.gstreamer.Closure;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pad;
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
		addMany(src, tee);
		Tool.successOrDie("src-tee", linkMany(src, tee));
		// don't join if already joined
		if (!rooms.containsKey(roomId)) {
			if (isPlaying()) {
				pause();
			}

			// create the sender bin
			SenderBin room = new SenderBin("room" + roomId, Config.BASE_IP
					+ roomId, Config.RTP_MULTICAST_PORT, true);
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

	public void stopStreamingTo(int roomId) {
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

		/*
		 * // create the sender bin SenderBin friend = new SenderBin("unicast_"
		 * + ip + "_" + port, ip, port, false); // add it to this add(friend);
		 * 
		 * // connect its input to the tee
		 * Tool.successOrDie("tee-unicastSender",
		 * tee.getRequestPad("src%d").link(friend.getStaticPad("sink"))
		 * .equals(PadLinkReturn.OK));
		 */
		Element convert = ElementFactory.make("audioconvert", "convert");
		Element rtpPayload = ElementFactory.make("rtppcmupay", "rtpPayload");
		Element encoder = ElementFactory.make("mulawenc", "mulawenc");
		Bin rtpBin = (Bin) ElementFactory.make("gstrtpbin", "rtpbin");
		rtpBin.connect(new Element.PAD_ADDED() {
			@Override
			public void padAdded(Element element, Pad pad) {
				System.out.println("Pad added: " + pad);
			}
		});
		rtpBin.getRequestPad("send_rtp_sink_0");
		rtpBin.connect("on-ssrc-validated", new Closure() {
			@SuppressWarnings("unused")
			public void invoke() {
				System.out.println("lol");
			}
		});
		System.out.println("Caps (check out SSRC) "
				+ rtpBin.getElementByName("rtpsession0").getSinkPads().get(0)
						.getCaps());

		Element rtpsink = ElementFactory.make("udpsink", "rtpsink");
		rtpsink.set("port", 5000);
		rtpsink.set("host", "130.240.53.166");
		rtpsink.set("auto-multicast", false);
		Element rtcpsink = ElementFactory.make("udpsink", "rtcpsink");
		rtcpsink.set("port", 5001);

		addMany(src, convert, rtpPayload, encoder, rtpBin, rtpsink, rtcpsink);
		System.out.println("link "
				+ Element.linkMany(src, convert, encoder, rtpPayload));

		System.out.println(Element.linkPads(rtpPayload, null, rtpBin,
				"send_rtp_sink_0"));
		System.out.println(Element.linkPads(rtpBin, "send_rtp_src_0", rtpsink,
				"sink"));

		System.out.println(Element.linkPads(rtpBin, "send_rtcp_src_0",
				rtcpsink, null));

		play();

		System.out.println(rtpBin.getSrcPads().get(1).getCaps());

		Gst.main();

	}

	public void stopStreamingTo(String ip, int port) {
		// TODO
	}
}
