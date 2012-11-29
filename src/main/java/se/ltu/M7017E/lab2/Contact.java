package se.ltu.M7017E.lab2;

public class Contact {

	private String name;
	private String ip;

	public Contact(String name, String ip) {
		this.name = name;

	}

	public Contact() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
