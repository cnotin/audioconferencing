package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;

public class UnicastSender {
	public static void main(String[] args) {

		String[] params = { "--gst-debug=3", "--gst-debug-no-color" };
		Gst.init("Sender", params);

		Pipeline pipeline = new Pipeline("pipeline");

		Element source = ElementFactory.make("audiotestsrc", "source");
		source.set("freq", new Long(5000));
		source.set("is-live", true);

		Element convert = ElementFactory.make("audioconvert", "convert");
		Element rtpPayload = ElementFactory.make("rtppcmupay", "rtpPayload");
		Element encoder = ElementFactory.make("mulawenc", "mulawenc");
		Bin rtpBin = (Bin) ElementFactory.make("gstrtpbin", "rtpbin");

		Element rtpsink = ElementFactory.make("udpsink", "rtpsink");
		rtpsink.set("port", 5000);
		rtpsink.set("host", "224.1.42.1");

		Element rtcpsink = ElementFactory.make("udpsink", "rtcpsink");
		rtcpsink.set("port", 5001);

		pipeline.addMany(source, convert, rtpPayload, encoder, rtpBin, rtpsink,
				rtcpsink);
		System.out.println("link "
				+ Element.linkMany(source, convert, encoder, rtpPayload));

		System.out.println(Element.linkPads(rtpPayload, null, rtpBin,
				"send_rtp_sink_0"));
		System.out.println(Element.linkPads(rtpBin, "send_rtp_src_0", rtpsink,
				"sink"));

		System.out.println(Element.linkPads(rtpBin, "send_rtcp_src_0",
				rtcpsink, null));

		pipeline.play();
		System.out.println(rtpBin.getSrcPads().get(1).getCaps());

		System.out.println("Src caps = "
				+ source.getSrcPads().get(0).getNegotiatedCaps());

		Gst.main();
	}
}
