package se.ltu.M7017E.lab2.ui;

import java.awt.Dimension;
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

public class AddContactDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7019751683904933564L;
	private JLabel nameLabel = new JLabel("Contact name");
	private JLabel IPLabel = new JLabel("Contact IP");
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");

	private final JTextField nameField = new JTextField(15);
	private final JTextField ipField = new JTextField(15);

	/**
	 * Window to add a contact thanks to his name and the IP address
	 */
	public AddContactDialog(final Gui gui) {
		this.setSize(400, 200);
		this.setTitle("Add a contact");
		this.setModal(true);

		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		namePanel.setVisible(true);

		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.X_AXIS));
		ipPanel.add(IPLabel);
		ipPanel.add(ipField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// save the contact
				addcontact(gui);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// destroy the window and the components
				dispose();
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(namePanel);
		mainPanel.add(ipPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(buttonPanel);

		this.add(mainPanel);
		this.setVisible(true);
	}

	/**
	 * Save the new contact in the "contact.txt" file
	 */

	private void addcontact(Gui gui) {
		try {

			String contact = nameField.getText() + "\n" + ipField.getText()
					+ "\n";
			FileWriter MyFile = new FileWriter("contacts.txt", true);
			MyFile.write(contact);

			MyFile.close();
			gui.setContactsList();
			gui.refreshJList();
			this.dispose();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}