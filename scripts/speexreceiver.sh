#!/bin/sh

SEND_TO_RTCP_PORT=5005
RECV_FROM_ADDR=127.0.0.1
RECV_FROM_RTP_PORT=5003
RECV_FROM_RTCP_PORT=5004
AUDIORATE=16000

gst-launch-0.10 -v --gst-debug=3 gstrtpbin name=rtpbin latency=200 \
udpsrc caps="application/x-rtp, media=(string)audio, clock-rate=(int)${AUDIORATE}, encoding-name=(string)SPEEX, encoding-params=(string)1, ssrc=(guint)419764010, payload=(int)110" port=${RECV_FROM_RTP_PORT} \
! rtpbin.recv_rtp_sink_1 \
rtpbin. ! rtpspeexdepay ! decodebin ! autoaudiosink \
udpsrc port=${RECV_FROM_RTCP_PORT} ! rtpbin.recv_rtcp_sink_1 \
rtpbin.send_rtcp_src_1 ! udpsink port=${SEND_TO_RTCP_PORT} host=${RECV_FROM_ADDR} sync=false async=false
