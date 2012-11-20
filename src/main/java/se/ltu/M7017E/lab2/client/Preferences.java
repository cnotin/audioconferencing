package se.ltu.M7017E.lab2.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Preferences {
	public static final int GROUP_PORT = 5000;
	public InetAddress GROUP_IP;
	public static final int BUFSIZE = 512;

	public Preferences() {
		try {
			GROUP_IP = InetAddress.getByName("224.1.1.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
