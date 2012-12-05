package se.ltu.M7017E.lab2.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact implements Comparable<Contact> {
	protected String name;
	protected String ip;

	@Override
	public int compareTo(Contact contact) {
		return this.name.compareTo(contact.name);
	}
}
