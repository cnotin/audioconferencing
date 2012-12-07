package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;

/**
 * This is a reusable RTP encoder bin which provides services of: queuing, audio
 * resampling, audio encoding and RTP payloading. The receiving part must use
 * the same codec as this so it's good to use this with {@link RtpDecodeBin}.
 */
public class RtpEncodeBin extends Bin {
	// queue to create a new thread for this branch
	private Element queue;
	// could be useful
	private Element resample;
	private Element encoder;
	private Element rtpPay;
	// helps to set the good stream parameters in this bin
	private Element capsFilter;

	private Pad sink;
	private Pad src;

	/**
	 * Create and add all necessary stuff.
	 */
	public RtpEncodeBin() {
		super();

		queue = ElementFactory.make("queue", null);
		resample = ElementFactory.make("audioconvert", null);

		capsFilter = ElementFactory.make("capsfilter", null);
		capsFilter.set("caps", Caps.fromString("audio/x-raw-int,rate=16000"));

		// speex codec, cf plugin's documentation
		encoder = ElementFactory.make("speexenc", null);
		encoder.set("quality", 6); // quality in [0,10]
		encoder.set("vad", true); // voice activity detection
		encoder.set("dtx", true); // discontinuous transmission

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
