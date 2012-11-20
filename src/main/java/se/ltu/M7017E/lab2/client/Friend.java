package se.ltu.M7017E.lab2.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
	private String name;
	private String ip;
	private Integer port;
}
