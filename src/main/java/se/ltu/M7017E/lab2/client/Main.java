package se.ltu.M7017E.lab2.client;

import java.util.Date;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import org.gstreamer.Bin;

import se.ltu.M7017E.lab2.client.ui.Gui;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		final App app = new App();

		if (args.length > 0 && args[0].equals("NO_UI")) {
			System.out.println("DEBUG: Don't display UI.");

			System.out.println("See the dot?");
			new java.util.Scanner(System.in).nextLine();
			app.getSender().debugToDotFile(Bin.DEBUG_GRAPH_SHOW_ALL, "sender");
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					final Gui gui = new Gui(app);
					app.setGui(gui);
					gui.setVisible(true);
				}
			});

			String s;
			Scanner scanner = new Scanner(System.in);
			do {
				System.out.println("See the dot?");
				s = scanner.next();
				long timestamp = new Date().getTime();
				app.getSender().debugToDotFile(Bin.DEBUG_GRAPH_SHOW_ALL,
						"lab2_" + timestamp + "_sender");
				app.getReceiver().debugToDotFile(Bin.DEBUG_GRAPH_SHOW_ALL,
						"lab2_" + timestamp + "_receiver");
			} while (s.equals("d"));
		}
		System.out.println("Bye ?");
		new java.util.Scanner(System.in).nextLine();
		System.out.println("See you");
		System.exit(0);
	}
}
