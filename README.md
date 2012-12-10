audioconferencing
=================

Audio conferencing system in Java using GStreamer and RTP over UDP.

This software enables you to talk with people by joining rooms or by direct call (you can join and talk in several rooms and one one-to-one conversation at the same time). Rooms are implemented through multicast and one-to-one with standard unicast but there is no NAT traversal. Therefore for both modes it will not work on NAT or routed networks.

We wanted to try to do this without any server (full P2P) but for the sake of simplicity we decided to have a central server just to manage presence of people. No audio is transferred through the server. Its code is available in this repository too.


This is an academic project for the course M7017E at Luleå Tekniska Universitet.