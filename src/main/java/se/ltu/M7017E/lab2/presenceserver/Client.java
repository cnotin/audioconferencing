package se.ltu.M7017E.lab2.presenceserver;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Server's clients. They have a name, an IP, and a TCP communication thread.
 */
@Data
@EqualsAndHashCode
public class Client {
	/** username */
	protected String name;
	/** TCP communication thread */
	private TCPThread tcpThread;

	public Client(TCPThread tcpThread) {
		this.tcpThread = tcpThread;
	}

	/**
	 * Send a message to the client, the toString method is automatically called
	 * on it.
	 * 
	 * @param message
	 *            any object with a toString method
	 */
	public void send(Object message) {
		tcpThread.send(message.toString());
	}

	/**
	 * Get IP adress of this Client.
	 * 
	 * @return IP adresse like "12.34.56.78"
	 */
	public String getIp() {
		return tcpThread.getIp();
	}
}
