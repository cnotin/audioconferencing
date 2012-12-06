#!/bin/sh

if [ $1 ]
then
	ROOM=$1
else
	echo "Gimme a room number"
	exit
fi;

gst-launch -v --gst-debug=3 gstrtpbin name=rtpbin \
    udpsrc multicast-group=224.1.42.$ROOM port=5000 caps="application/x-rtp, media=(string)audio, clock-rate=(int)80000, encoding-name=(string)PCMU, channels=(int)1, payload=(int)0" ! rtpbin.recv_rtp_sink_0 \
        rtpbin. ! rtppcmudepay ! mulawdec ! audioconvert ! autoaudiosink
