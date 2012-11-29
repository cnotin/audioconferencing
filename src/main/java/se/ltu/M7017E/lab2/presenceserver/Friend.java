package se.ltu.M7017E.lab2.presenceserver;

import lombok.Data;
import lombok.EqualsAndHashCode;
import se.ltu.M7017E.lab2.common.Contact;

@Data
@EqualsAndHashCode(callSuper = true)
public class Friend extends Contact {
	private TCPThread tcpThread;

	public void send(Object message) {
		tcpThread.send(message.toString());
	}
}
