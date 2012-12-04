package se.ltu.M7017E.lab2.client.audio;

import java.net.UnknownHostException;

import org.gstreamer.Gst;

public class UnicastSender {
	public static void main(String[] args) throws UnknownHostException {
		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=2",
				"--gst-debug-no-color" });
		SenderPipeline sp = new SenderPipeline();
		sp.streamTo("130.240.53.166", 5000);
	}
}
