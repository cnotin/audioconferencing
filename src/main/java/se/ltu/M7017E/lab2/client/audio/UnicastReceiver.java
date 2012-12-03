package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;

import se.ltu.M7017E.lab2.client.Tool;

public class UnicastReceiver extends Bin {

	private final Element udpSource;
	private final Element rtpBin;

	public UnicastReceiver(String ip, int port) {
		udpSource = ElementFactory.make("udpsrc", null);
		udpSource.set("port", port);
		Tool.successOrDie("caps",
				udpSource.getStaticPad("src").setCaps(
						Caps.fromString("application/x-rtp, media=audio, "
								+ "clock-rate=8000, channel=1, payload=0, "
								+ "encoding-name=PCMU")));
		rtpBin = ElementFactory.make("gstrtpbin", null);
		RtpMulawDecodeBin decoder = new RtpMulawDecodeBin();
		addMany(udpSource, rtpBin, decoder);

	}
}
