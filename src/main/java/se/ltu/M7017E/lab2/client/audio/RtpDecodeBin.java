package se.ltu.M7017E.lab2.client.audio;

import lombok.AllArgsConstructor;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;

public class RtpDecodeBin extends Bin {
	private Element rtpDepay;
	private Element decoder;
	private Element convert;

	private Pad sink;
	private Pad src;

	public RtpDecodeBin(boolean autoDisconnect) {
		super();

		rtpDepay = ElementFactory.make("rtpspeexdepay", null);
		decoder = ElementFactory.make("speexdec", null);
		convert = ElementFactory.make("audioconvert", null);

		this.addMany(rtpDepay, decoder, convert);
		Bin.linkMany(rtpDepay, decoder, convert);

		sink = new GhostPad("sink", rtpDepay.getStaticPad("sink"));
		src = new GhostPad("src", convert.getStaticPad("src"));

		this.addPad(sink);
		this.addPad(src);

		if (autoDisconnect) {
			this.sink.connect(new OnPadUnlinked(this));
		}
	}

	/**
	 * Called to cleanly remove this Bin from its parent. Assumption: it was
	 * connected to downstream through a request pad (that will also be cleanly
	 * released)
	 */
	public void getOut() {
		// clean request pad from adder
		Pad downstreamPeer = src.getPeer();
		downstreamPeer.getParentElement().releaseRequestPad(downstreamPeer);

		System.out.println("Remove from parent bin "
				+ ((Bin) this.getParent()).remove(this));
	}

	@AllArgsConstructor
	private class OnPadUnlinked implements GhostPad.UNLINKED {
		RtpDecodeBin parentBin;

		@Override
		public void unlinked(Pad complainer, Pad gonePad) {
			if (gonePad.getDirection().equals(PadDirection.SRC)) {
				parentBin.getOut();
			}
		}
	}
}
