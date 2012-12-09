package se.ltu.M7017E.lab2.client.audio;

import java.util.List;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.State;
import org.gstreamer.elements.Tee;
import org.gstreamer.elements.good.RTPBin;

import se.ltu.M7017E.lab2.client.Tool;

/**
 * Bin made to be added to {@link SenderPipeline}. Takes care of encoding, and
 * RTP preparing then sending over UDP the audio input.<br />
 * Can be used to send to a multicast group or unicast peer. Cf
 * {@link SenderBin#SenderBin(String, String, int, boolean) boolean "multicast"
 * parameter}
 */
public class SenderBin extends Bin {
	private final Pad sink;
	private final RtpEncodeBin encoder;
	private final Element udpSink;
	private final RTPBin rtpBin;

	/**
	 * Create, configure and setup everything needed.
	 * 
	 * @param name
	 *            GStreamer element name
	 * @param ip
	 *            IP to send to, can be private or public or multicast (if
	 *            multicast then the group will be automatically joined)
	 * @param port
	 *            UDP port to send to
	 * @param multicast
	 *            set to True if this will be multicasting, False for unicast
	 */
	public SenderBin(String name, String ip, int port, boolean multicast) {
		super(name);

		encoder = new RtpEncodeBin();
		encoder.syncStateWithParent();
		rtpBin = new RTPBin((String) null);
		// asking this put the gstrtpbin plugin in sender mode
		Pad rtpSink0 = rtpBin.getRequestPad("send_rtp_sink_0");

		udpSink = ElementFactory.make("udpsink", null);
		udpSink.set("host", ip);
		udpSink.set("port", port);
		if (multicast) {
			// make OS automatically join multicast group
			udpSink.set("auto-multicast", true);
		}
		udpSink.set("async", false);

		// ############## ADD THEM TO PIPELINE ####################
		addMany(encoder, rtpBin, udpSink);

		// ###################### LINK THEM ##########################
		sink = new GhostPad("sink", encoder.getStaticPad("sink"));
		sink.setActive(true);
		addPad(sink);

		Tool.successOrDie(
				"encoder-rtpBin",
				encoder.getStaticPad("src").link(rtpSink0)
						.equals(PadLinkReturn.OK));
		Tool.successOrDie(
				"rtpbin-udpSink",
				rtpBin.getStaticPad("send_rtp_src_0")
						.link(udpSink.getStaticPad("sink"))
						.equals(PadLinkReturn.OK));
	}

	/**
	 * Get the (first) Element from the list which name starts with 'start'. So
	 * for example if you want the element "rtpsession" no matter if it's really
	 * called "rtpsession0" or "rtpsession2", you should call this with
	 * start="rtpsession".
	 * 
	 * @param elts
	 *            list of Element_s to search in
	 * @param start
	 *            the String to search at the beginning
	 * @return the (first, if several) Element. Or null if no name matched.
	 */
	private Element getElementByNameStartingWith(List<Element> elts,
			String start) {
		Element ret = null;

		for (Element elt : elts) {
			if (elt.getName().startsWith(start)) {
				return elt;
			}
		}

		return ret;
	}

	/**
	 * Get my SSRC as a sender (from gstrtpbin session manager)
	 * 
	 * @return
	 */
	public Long getSSRC() {
		/*
		 * we dig to find it in sink pad's caps by parsing these as a string,
		 * didn't find any better way
		 */
		String caps = getElementByNameStartingWith(rtpBin.getElements(),
				"rtpsession").getSinkPads().get(0).getCaps().toString();
		int ssrcBegin = caps.indexOf("ssrc=(uint)") + 11;
		int ssrcEnd = caps.indexOf(";", ssrcBegin);
		return new Long(caps.substring(ssrcBegin, ssrcEnd));
	}

	/**
	 * Called to cleanly remove this Bin from its parent. Assumption: it was
	 * connected upstream through a request pad (that will also be cleanly
	 * released)
	 */
	public void getOut() {
		// clean request pad from adder
		Pad upstreamPeer = sink.getPeer();
		Tee teeUpstream = ((Tee) sink.getPeer().getParent());
		Bin parentBin = ((Bin) this.getParent());

		upstreamPeer.setBlocked(true);

		this.setState(State.NULL);
		System.out.println("Remove from parent bin " + parentBin.remove(this));

		/*
		 * if upstream tee has no src anymore, the pipeline will push in the
		 * void and crash, thus we avoid it by stopping the whole bin
		 */
		if (teeUpstream.getSrcPads().size() == 1) {
			parentBin.setState(State.NULL);
		}
		teeUpstream.releaseRequestPad(upstreamPeer);

		System.out.println(teeUpstream.getPads());
	}
}
