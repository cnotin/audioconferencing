package se.ltu.M7017E.lab2.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import se.ltu.M7017E.lab2.App;

public class Gui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9219551892569083659L;
	private App app;
	private JMenuBar menu;
	private JButton callBtn;
	private JButton hangUpBtn;
	private JButton addBtn;
	private JButton rmvBtn;
	private JList contactsList;
	private JList contactsToCallList;
	private JTree roomList = new JTree();

	private ImageIcon addIcon = new ImageIcon(getClass().getResource(
			"/icons/add_button.png"));
	private ImageIcon rmvIcon = new ImageIcon(getClass().getResource(
			"/icons/rmv_button.png"));
	private ImageIcon callIcon = new ImageIcon(getClass().getResource(
			"/icons/call_button.png"));
	private ImageIcon hangIcon = new ImageIcon(getClass().getResource(
			"/icons/hang_button.png"));

	public Gui(final App app) {
		this.setJMenuBar(createMenu());
		this.app = app;
		this.setTitle("Audio conferencing tool");
		this.setSize(600, 500);
		this.setResizable(true);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// use OS' native look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Slider.paintValue", false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		this.add(createButtonPanel());
		this.add(createContactsPanel());

	}

	private JPanel createContactsPanel() {

		JPanel panel = new JPanel();

		JPanel roomPanel = new JPanel();
		JPanel contactPanel = new JPanel();
		JPanel buttons = new JPanel();

		addBtn = new JButton(addIcon);
		rmvBtn = new JButton(rmvIcon);

		/* Room Panel */
		roomList = buildTree();
		roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
		JScrollPane roomListPane = new JScrollPane(roomList);
		roomPanel.add(new JLabel("Room list"));
		roomPanel.add(roomListPane);

		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
		contactsList = new JList();
		JScrollPane contactToCallScrollPane = new JScrollPane(
				this.contactsToCallList);
		contactPanel.add(new JLabel("Contact list"));
		contactPanel.add(contactToCallScrollPane);

		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		buttons.setAlignmentX(CENTER_ALIGNMENT);
		buttons.add(addBtn);
		buttons.add(rmvBtn);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(roomPanel);
		panel.add(buttons);
		panel.add(contactPanel);

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		callBtn = new JButton("Call contact", callIcon);
		hangUpBtn = new JButton("Hang up", hangIcon);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(callBtn);
		panel.add(hangUpBtn);
		return panel;
	}

	private JMenuBar createMenu() {
		this.menu = new JMenuBar();

		JMenu edit = new JMenu("Edit");
		JMenuItem addContact = new JMenuItem("Add a contact");
		addContact.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JDialog jdialog = new JDialog();
				JPanel mainPanel = new JPanel();
				JPanel namePanel = new JPanel();
				JPanel ipPanel = new JPanel();
				JPanel buttonPanel = new JPanel();
				JLabel contactNameLabel = new JLabel("Contact name");
				JLabel contactIPLabel = new JLabel("Contact IP");
				final JTextField nameField = new JTextField(20);
				final JTextField ipField = new JTextField(15);
				JButton saveButton = new JButton("Save");
				JButton cancelButton = new JButton("Cancel");
				jdialog.setSize(400, 200);
				jdialog.setTitle("Add a contact");
				jdialog.setVisible(true);

				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
				namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
				ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.X_AXIS));
				buttonPanel.setLayout(new BoxLayout(buttonPanel,
						BoxLayout.X_AXIS));

				namePanel.add(contactNameLabel);
				namePanel.add(nameField);
				ipPanel.add(contactIPLabel);
				ipPanel.add(ipField);
				buttonPanel.add(saveButton);
				buttonPanel.add(cancelButton);
				mainPanel.add(namePanel);
				mainPanel.add(ipPanel);
				mainPanel.add(buttonPanel);

				jdialog.add(mainPanel);
				saveButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("plouf");
						try {

							String contact = nameField.getText() + "\n"
									+ ipField.getText() + "\n";
							FileWriter MyFile = new FileWriter("contacts.txt",
									true);
							MyFile.write(contact);

							MyFile.close();

							FileReader fr = new FileReader("contacts.txt");
							BufferedReader br = new BufferedReader(fr);
							String s;
							int i = 0;
							while ((s = br.readLine()) != null) {
								i++;
								System.out.println(s);
							}
							System.out.println(i);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						;
					}

				});

			}
		});
		edit.add(addContact);
		menu.add(edit);

		JMenu help = new JMenu("?");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								Gui.this,
								"This is a wonderful Audio conferencing tool, really.\nMade by: THE FRENCHIES!\n\n"
										+ "· Flore Diallo\n"
										+ "· Hervé Loeffel\n"
										+ "· Clément Notin", null,
								JOptionPane.INFORMATION_MESSAGE);
			}
		});
		help.add(about);

		menu.add(help);

		return menu;
	}

	private JTree buildTree() {
		// Root creation
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode("All rooms");

		// Add leaves and way from the root
		for (int i = 1; i < 6; i++) {
			DefaultMutableTreeNode rep = new DefaultMutableTreeNode("Room" + i);

			// Add 4 ways
			if (i < 4) {
				DefaultMutableTreeNode rep2 = new DefaultMutableTreeNode(
						"Contact" + i);
				rep.add(rep2);
			}
			// Add leaves to the root
			racine.add(rep);
		}
		// create tree
		JTree arbre = new JTree(racine);
		return arbre;
	}
}