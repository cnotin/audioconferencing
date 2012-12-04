#!/bin/sh

if [ $1 ]
then
	PORT=$1
else
	echo "Gimme a port to send to"
	exit
fi;

if [ $2 ]
then
	FREQ=$2
else
	FREQ=440;
fi;



gst-launch -v --gst-debug=2 gstrtpbin name=rtpbin \
    audiotestsrc freq=$FREQ ! tee name=t \
    t. ! audioconvert ! mulawenc ! rtppcmupay ! rtpbin.send_rtp_sink_0 \
          rtpbin.send_rtp_src_0 ! udpsink port=$PORT
