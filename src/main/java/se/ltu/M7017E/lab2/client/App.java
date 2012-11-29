package se.ltu.M7017E.lab2.client;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import se.ltu.M7017E.lab2.common.Contact;

public class App {
	private ControlChannel control;
	@Getter
	private List<Contact> contacts = new LinkedList<Contact>();
	@Getter
	private Contact me = new Contact("lab2 client", "lab2.client.lan");

	public App() {
		control = new ControlChannel(this);
		new Thread(control).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Contact findcontactsByName(String name) {
		for (Contact contact : contacts) {
			if (contact.getName().equals(name)) {
				return contact;
			}
		}

		return null;
	}
}
