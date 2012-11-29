package se.ltu.M7017E.lab2.client;

import javax.swing.SwingUtilities;

import se.ltu.M7017E.lab2.client.ui.Gui;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		final App app = new App();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui(app);
				app.setGui(gui);
				gui.setVisible(true);
			}
		});
	}
}
