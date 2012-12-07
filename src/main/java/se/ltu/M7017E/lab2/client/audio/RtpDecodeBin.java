package se.ltu.M7017E.lab2.client.audio;

import lombok.AllArgsConstructor;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;

/**
 * This is a reusable RTP decoder bin which provides services of: RTP depayload,
 * audio decoding, audio converting. This must use the same codec as the sender
 * so it's better to use this with {@link RtpEncodeBin} encoded stream.
 */
public class RtpDecodeBin extends Bin {
	private Element rtpDepay;
	private Element decoder;
	private Element convert;

	private Pad sink;
	private Pad src;

	/**
	 * Create and add all elements.
	 * 
	 * @param autoDisconnect
	 *            If true, then this will automatically disconnect from
	 *            downstream element when upstream pad is unlinked. Otherwise it
	 *            just ignores this event and hope that the parent Bin will take
	 *            care of disconnecting it. We use both modes in this project.
	 */
	public RtpDecodeBin(boolean autoDisconnect) {
		super();

		// this is a speex encoded payload
		rtpDepay = ElementFactory.make("rtpspeexdepay", null);
		// use speex codec
		decoder = ElementFactory.make("speexdec", null);
		convert = ElementFactory.make("audioconvert", null);

		this.addMany(rtpDepay, decoder, convert);
		Bin.linkMany(rtpDepay, decoder, convert);

		// create Bin's pads
		sink = new GhostPad("sink", rtpDepay.getStaticPad("sink"));
		src = new GhostPad("src", convert.getStaticPad("src"));

		this.addPad(sink);
		this.addPad(src);

		if (autoDisconnect) {
			// detect unlinking of sink pad (= upstream peer is gone)
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
