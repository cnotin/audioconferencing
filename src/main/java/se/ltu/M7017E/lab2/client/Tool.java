package se.ltu.M7017E.lab2.client;


public class Tool {
	/**
	 * For some severe and unrecoverable errors, it's better to shutdown the
	 * application instead of continuing in a degraded state.
	 * 
	 * @param message
	 *            Message to show, please explain the problem.
	 * @param result
	 *            if False: exit, if True: nothing to do, program continues
	 */
	public static void successOrDie(String message, boolean result) {
		if (!result) {
			System.err.println("Die because of " + message);
			System.exit(-1);
		}
	}
}
