package se.ltu.M7017E.lab2.client;

import javax.swing.SwingUtilities;

import org.gstreamer.Bin;

import se.ltu.M7017E.lab2.client.ui.Gui;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		final App app = new App();

		if (args.length > 0 && args[0].equals("NO_UI")) {
			System.out.println("DEBUG: Don't display UI.");

			// TODO, remove. This is an example. It shouldn't be done this way
			// at all.
			int port = app.getReceiver().receiveFromUnicast();
			app.getSender().streamTo("127.0.0.01", port);

			System.out.println("See the dot?");
			new java.util.Scanner(System.in).nextLine();
			app.getSender().debugToDotFile(Bin.DEBUG_GRAPH_SHOW_ALL, "sender");

			System.out.println("Bye ?");
			new java.util.Scanner(System.in).nextLine();
			System.out.println("See you");
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
