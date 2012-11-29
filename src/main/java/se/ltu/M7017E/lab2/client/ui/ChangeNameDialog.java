package se.ltu.M7017E.lab2.client.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChangeNameDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7019751683904933564L;
	private JLabel nameLabel = new JLabel("Name :");
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");

	private final JTextField nameField = new JTextField(15);

	/**
	 * Window to change the name of the user
	 */
	public ChangeNameDialog(final Gui gui) {
		this.setSize(400, 80);
		this.setTitle("Insert your name");
		this.setModal(true);

		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		nameField.setText(gui.getUserName());
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout());
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		namePanel.setVisible(true);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// save the contact
				if (!nameField.getText().equals(""))
					changeName(gui);

			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gui.getUserName() != null)
					dispose();
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(namePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(buttonPanel);

		this.add(mainPanel);
		this.setVisible(true);
	}

	/**
	 * Save the name in the "name.txt" file
	 */
	private void changeName(Gui gui) {
		try {

			String name = nameField.getText();
			FileWriter myFile = new FileWriter("name.txt", false);
			myFile.write(name);

			myFile.close();
			this.dispose();
			gui.setGuiTitleWithName();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}