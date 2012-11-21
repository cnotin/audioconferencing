package se.ltu.M7017E.lab2.presenceserver;

import lombok.Data;

@Data
public class Friend {
	private String name;
	private TCPThread tcpThread;

	public void send(Object message) {
		tcpThread.send(message.toString());
	}
}
