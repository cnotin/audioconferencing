package se.ltu.M7017E.lab2.presenceserver;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Client {
	protected String name;
	protected String ip;
	private TCPThread tcpThread;

	public Client(String ip, TCPThread tcpThread) {
		this.ip = ip;
		this.tcpThread = tcpThread;
	}

	public void send(Object message) {
		tcpThread.send(message.toString());
	}
}
