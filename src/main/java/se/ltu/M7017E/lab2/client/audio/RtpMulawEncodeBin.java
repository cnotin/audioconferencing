package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;

public class RtpMulawEncodeBin extends Bin {
	private Element convert;
	private Element encoder;
	private Element rtpPay;

	private Pad sink;
	private Pad src;

	public RtpMulawEncodeBin() {
		super();

		convert = ElementFactory.make("audioconvert", null);
		encoder = ElementFactory.make("mulawenc", null);
		rtpPay = ElementFactory.make("rtppcmupay", null);

		this.addMany(convert, encoder, rtpPay);
		Bin.linkMany(convert, encoder, rtpPay);

		sink = new GhostPad("sink", convert.getStaticPad("sink"));
		sink.setActive(true);
		src = new GhostPad("src", rtpPay.getStaticPad("src"));
		src.setActive(true);

		this.addPad(sink);
		this.addPad(src);
	}
}
