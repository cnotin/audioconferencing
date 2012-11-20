package se.ltu.M7017E.lab2.client;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class App {
	private ControlChannel control;
	@Getter
	private List<Friend> friends = new LinkedList<Friend>();
	@Getter
	private Friend me = new Friend("lab2 client", "lab2.client.lan", 1200);

	public App() {
		control = new ControlChannel(this);
		new Thread(control).start();
		control.whosThere();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("End of timeout");
		control.iAm(me);
	}

	public Friend findFriendByName(String name) {
		for (Friend friend : friends) {
			if (friend.getName().equals(name)) {
				return friend;
			}
		}

		return null;
	}
}
