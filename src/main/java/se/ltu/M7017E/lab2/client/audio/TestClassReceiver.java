package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Gst;
import org.gstreamer.Pipeline;

//class to del
public class TestClassReceiver {

	public static void main(String[] args) {
		Gst.init("Audioconferencing", new String[] { "--gst-debug-level=3",
				"--gst-debug-no-color" });
		Pipeline pipeline = new Pipeline();
		pipeline.add(new UnicastReceiver("224.1.42.1", 5000));
		System.out.println("blblbl");
		pipeline.play();

	}

}
