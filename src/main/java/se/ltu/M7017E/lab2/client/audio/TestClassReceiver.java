package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;

//class to del
public class TestClassReceiver {

	public static void main(String[] args) {
		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=3",
				"--gst-debug-no-color" });
		Pipeline pipeline = new Pipeline();
		pipeline.pause();

		Element sink = ElementFactory.make("autoaudiosink", null);
		Element receiver = new UnicastReceiver(sink);

		pipeline.addMany(receiver, sink);
		Bin.linkMany(receiver, sink);

		pipeline.play();
		Gst.main();
	}

}
