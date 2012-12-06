package se.ltu.M7017E.lab2.client.audio;

import lombok.Getter;

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

public class SenderBin extends Bin {
	private final Pad sink;
	@Getter
	private final RtpMulawEncodeBin encoder;
	private final Element udpSink;
	private final RTPBin rtpBin;

	public SenderBin(String name, String ip, int port, boolean multicast) {
		super(name);

		encoder = new RtpMulawEncodeBin();
		encoder.syncStateWithParent();
		rtpBin = new RTPBin((String) null);
		Pad rtpSink0 = rtpBin.getRequestPad("send_rtp_sink_0");

		udpSink = ElementFactory.make("udpsink", null);
		udpSink.set("host", ip);
		udpSink.set("port", port);
		if (multicast) {
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

	public Long getSSRC() {
		String caps = Tool
				.getElementByNameStartingWith(rtpBin.getElements(),
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

		// if upstream tee has no src anymore, the pipeline will push in the
		// void and crash, thus we avoid it
		if (teeUpstream.getSrcPads().size() == 1) {
			parentBin.setState(State.NULL);
		}
		teeUpstream.releaseRequestPad(upstreamPeer);

		System.out.println(teeUpstream.getPads());
	}
}
