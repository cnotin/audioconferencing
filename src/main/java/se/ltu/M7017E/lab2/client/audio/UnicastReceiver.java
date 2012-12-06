package se.ltu.M7017E.lab2.client.audio;

import lombok.Getter;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.State;

import se.ltu.M7017E.lab2.client.Tool;

public class UnicastReceiver extends Bin {
	private final Element udpSource;
	private final Element rtpBin;
	private Pad src;
	@Getter
	private int port = 0;

	/**
	 * TODO
	 * 
	 * @param connectSrcTo
	 *            As soon as our friend will have called us on this local
	 *            unicast port, we will connect the src of this bin to this
	 *            Element
	 */
	public UnicastReceiver(String name, final Element connectSrcTo) {
		super(name);

		udpSource = ElementFactory.make("udpsrc", null);
		udpSource.set("port", 0);
		Tool.successOrDie("caps",
				udpSource.getStaticPad("src").setCaps(
						Caps.fromString("application/x-rtp, media=audio, "
								+ "clock-rate=8000, channel=1, payload=0, "
								+ "encoding-name=PCMU")));

		rtpBin = ElementFactory.make("gstrtpbin", null);

		// ####################### CONNECT EVENTS ######################"
		rtpBin.connect(new Element.PAD_ADDED() {
			@Override
			public void padAdded(Element element, Pad pad) {
				if (pad.getName().startsWith("recv_rtp_src")) {
					System.out.println("Got new sound input pad: " + pad);
					// create elements
					RtpMulawDecodeBin decoder = new RtpMulawDecodeBin(false);

					// add them
					UnicastReceiver.this.add(decoder);

					// sync them
					decoder.syncStateWithParent();

					// link them
					Tool.successOrDie(
							"bin-decoder",
							pad.link(decoder.getStaticPad("sink")).equals(
									PadLinkReturn.OK));

					/*
					 * now that we have what we should connect to it, add the
					 * ghost pad
					 */
					src = new GhostPad("src", decoder.getStaticPad("src"));
					src.setActive(true);
					addPad(src);

					/*
					 * connect this UnicastReceiver to the Element we've been
					 * asked to do
					 */
					Tool.successOrDie("unicastreceiver-connectsrcto", Element
							.linkMany(UnicastReceiver.this, connectSrcTo));
				}
			}
		});

		// ############## ADD THEM TO PIPELINE ####################
		addMany(udpSource, rtpBin);

		// ###################### LINK THEM ##########################
		Pad pad = rtpBin.getRequestPad("recv_rtp_sink_0");
		Tool.successOrDie("udpSource-rtpbin", udpSource.getStaticPad("src")
				.link(pad).equals(PadLinkReturn.OK));

		pause();

		port = (Integer) udpSource.get("port");
		System.out.println("Got assigned port: " + port);
	}

	/**
	 * Called to cleanly remove this Bin from its parent. Assumption: it was
	 * connected downstream through a request pad (that will also be cleanly
	 * released)
	 */
	public void getOut() {
		/*
		 * if we were connected to something downstream (may haven't been the
		 * cause if call was refused for example)
		 */
		Pad downstreamPeer = null;
		if (src != null) {
			// before disconnecting, remember the request pad we were linked to
			downstreamPeer = src.getPeer();
		}

		this.setState(State.NULL);

		System.out.println("Remove from parent bin "
				+ ((Bin) this.getParent()).remove(this));

		if (downstreamPeer != null) {
			// clean request pad from adder
			downstreamPeer.getParentElement().releaseRequestPad(downstreamPeer);
		}
	}
}
