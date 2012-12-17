#!/bin/sh

SEND_TO_ADDR=127.0.0.1
SEND_TO_RTP_PORT=5003
SEND_TO_RTCP_PORT=5004
RECV_FROM_RTCP_PORT=5005

ENCODER=speexenc
PAYLOADER=rtpspeexpay

SPEEX_PARAMS="quality=6 vad=true dtx=true"
SPEEX_CAPS="audio/x-raw-int,rate=16000"

ENCODER_PARAMS=${SPEEX_PARAMS}
RTP_PARAMS="latency=100"
AUDIO_CAPS=${SPEEX_CAPS}

gst-launch -v --gst-debug=3 gstrtpbin name=rtpbin ${RTP_PARAMS} \
autoaudiosrc \
! queue ! audioresample ! ${AUDIO_CAPS} ! ${ENCODER} ${ENCODER_PARAMS} ! ${PAYLOADER} \
! rtpbin.send_rtp_sink_1 \
rtpbin.send_rtp_src_1 ! udpsink port=${SEND_TO_RTP_PORT} host=${SEND_TO_ADDR} \
rtpbin.send_rtcp_src_1 ! udpsink port=${SEND_TO_RTCP_PORT} host=${SEND_TO_ADDR} sync=false async=false \
udpsrc port=${RECV_FROM_RTCP_PORT} ! rtpbin.recv_rtcp_sink_1
