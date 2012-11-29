package se.ltu.M7017E.lab2.client;

public class Tool {
	// TODO delete this method, replace with exceptions and handling
	public static void successOrDie(String message, boolean result) {
		if (!result) {
			System.err.println("Die because of " + message);
			System.exit(-1);
		}
	}
}
