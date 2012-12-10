package se.ltu.M7017E.lab2.client;

/** Configuration constants */
public class Config {
	/**
	 * Holds the first 3 octets of the multicast IP including last dot. Just
	 * concatenate the room number to get the IP.
	 */
	public static final String BASE_IP = "224.1.42.";

	/** Port to send RTP multicast stream. */
	public static final int RTP_MULTICAST_PORT = 5000;

	/** Server address (IP or hostname) */
	// public static final String SERVER_ADDRESS = "130.240.92.20";
	public static final String SERVER_ADDRESS = "localhost";

	/** Server TCP port */
	public static final int SERVER_PORT = 4000;
}
