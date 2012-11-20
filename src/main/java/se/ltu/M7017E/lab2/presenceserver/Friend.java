package se.ltu.M7017E.lab2.presenceserver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
	private String name;
	private String ip;
	private Integer port;
	private Client client;

	public Friend(String name) {
		this.name = name;
	}
}
