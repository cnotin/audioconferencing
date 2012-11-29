package se.ltu.M7017E.lab2.client;

public class Config {
	/**
	 * Holds the first 3 octets of the multicast IP including last dot. Just
	 * concatenate the room number to get the IP.
	 */
	public static final String BASE_IP = "224.1.42.";

	/**
	 * Port to send RTP multicast stream.
	 */
	public static final int RTP_MULTICAST_PORT = 5000;
}
