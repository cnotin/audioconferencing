package se.ltu.M7017E.lab2.client.audio;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.PadLinkReturn;

import se.ltu.M7017E.lab2.client.Tool;

public class RoomReceiver extends Bin {
	private final Element udpSource;
	private final Element rtpBin;
	private final Element adder;
	private final Pad src;

	public RoomReceiver(String name, String ip, int port) {
		super(name);

		udpSource = ElementFactory.make("udpsrc", null);
		udpSource.set("multicast-group", ip);
		udpSource.set("port", port);
		udpSource.set("auto-multicast", true);

		Tool.successOrDie("caps",
				udpSource.getStaticPad("src").setCaps(
						Caps.fromString("application/x-rtp, media=audio, "
								+ "clock-rate=8000, channel=1, payload=0, "
								+ "encoding-name=PCMU")));

		rtpBin = ElementFactory.make("gstrtpbin", null);
		adder = ElementFactory.make("liveadder", null);

		// ####################### CONNECT EVENTS ######################"
		rtpBin.connect(new Element.PAD_ADDED() {
			@Override
			public void padAdded(Element element, Pad pad) {
				if (pad.getName().startsWith("recv_rtp_src")) {
					/*
					 * TODO : if SSRC=myself.ssrc then connect to fakesink to
					 * prevent echo of my own voice
					 */

					System.out.println("Got new sound input pad: " + pad);

					// create elements
					RtpMulawDecodeBin decoder = new RtpMulawDecodeBin();

					// add them
					RoomReceiver.this.add(decoder);

					// sync them
					decoder.syncStateWithParent();

					// link them
					Tool.successOrDie(
							"bin-decoder",
							pad.link(decoder.getStaticPad("sink")).equals(
									PadLinkReturn.OK));

					Pad adderPad = adder.getRequestPad("sink%d");
					Tool.successOrDie(
							"decoder-adder",
							decoder.getStaticPad("src").link(adderPad)
									.equals(PadLinkReturn.OK));
				}
			}
		});

		// ############## ADD THEM TO PIPELINE ####################
		addMany(udpSource, rtpBin, adder);

		// Now they are in the pipeline, we can add the ghost pad
		src = new GhostPad("src", adder.getStaticPad("src"));
		addPad(src);

		// ###################### LINK THEM ##########################
		Pad pad = rtpBin.getRequestPad("recv_rtp_sink_0");
		Tool.successOrDie("udpSource-rtpbin", udpSource.getStaticPad("src")
				.link(pad).equals(PadLinkReturn.OK));

		pause();
	}
}
