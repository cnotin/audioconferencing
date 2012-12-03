package se.ltu.M7017E.lab2.client;

import javax.swing.SwingUtilities;

import se.ltu.M7017E.lab2.client.ui.Gui;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		final App app = new App();
		if (args.length > 0 && args[0].equals("NO_UI")) {
			System.out.println("Don't display UI. Press enter to quit");

			app.joinRoom(1);
			app.joinRoom(2);

			new java.util.Scanner(System.in).nextLine();
			System.out.println("Bye");
			System.exit(0);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Gui gui = new Gui(app);
					app.setGui(gui);
					gui.setVisible(true);
				}
			});
		}

	}
}
