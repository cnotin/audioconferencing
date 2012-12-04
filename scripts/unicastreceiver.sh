#!/bin/sh

if [ $1 ]
then
	PORT=$1
else
	echo "Gimme a port number"
	exit
fi;

gst-launch -v --gst-debug=2 gstrtpbin name=rtpbin \
    udpsrc port=$PORT caps="application/x-rtp, media=(string)audio, clock-rate=(int)8000, encoding-name=(string)PCMU, channels=(int)1, payload=(int)0" ! rtpbin.recv_rtp_sink_0 \
        rtpbin. ! rtppcmudepay ! mulawdec ! audioconvert ! autoaudiosink
