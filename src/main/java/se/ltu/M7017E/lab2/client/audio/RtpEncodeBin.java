package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;

public class RtpEncodeBin extends Bin {
	private Element queue;
	private Element resample;
	private Element encoder;
	private Element rtpPay;
	private Element capsFilter;

	private Pad sink;
	private Pad src;

	public RtpEncodeBin() {
		super();

		queue = ElementFactory.make("queue", null);
		resample = ElementFactory.make("audioconvert", null);

		capsFilter = ElementFactory.make("capsfilter", null);
		capsFilter.set("caps", Caps.fromString("audio/x-raw-int,rate=16000"));

		encoder = ElementFactory.make("speexenc", null);
		encoder.set("quality", 6);
		encoder.set("vad", true);
		encoder.set("dtx", true);

		rtpPay = ElementFactory.make("rtpspeexpay", null);

		this.addMany(queue, resample, capsFilter, encoder, rtpPay);
		Bin.linkMany(queue, resample, capsFilter, encoder, rtpPay);

		sink = new GhostPad("sink", queue.getStaticPad("sink"));
		sink.setActive(true);
		src = new GhostPad("src", rtpPay.getStaticPad("src"));
		src.setActive(true);

		this.addPad(sink);
		this.addPad(src);
	}
}
