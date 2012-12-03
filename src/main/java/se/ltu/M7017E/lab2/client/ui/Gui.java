package se.ltu.M7017E.lab2.client.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import se.ltu.M7017E.lab2.client.App;
import se.ltu.M7017E.lab2.client.ControlChannel;
import se.ltu.M7017E.lab2.common.Contact;
import se.ltu.M7017E.lab2.common.messages.Call;

public class Gui extends JFrame {

	private static final long serialVersionUID = -9219551892569083659L;
	private JMenuBar menu;
	private JButton callBtn;
	private JButton hangUpBtn;
	private JButton newBtn;
	private JButton dltBtn;
	private JList contactsList;
	private ArrayList<Contact> contacts;
	public JTree roomList;
	private ImageIcon callIcon = new ImageIcon(getClass().getResource(
			"/icons/call_button.png"));
	private ImageIcon hangIcon = new ImageIcon(getClass().getResource(
			"/icons/hang_button.png"));
	private ImageIcon newIcon = new ImageIcon(getClass().getResource(
			"/icons/new_button.png"));
	private ImageIcon dltIcon = new ImageIcon(getClass().getResource(
			"/icons/dlt_button.png"));

	private DefaultListModel model;
	private String userName;
	private App app;

	/**
	 * Interface of the application. Display the main window
	 */
	public Gui(final App app) {
		// use OS' native look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.app = app;
		this.setGuiTitleWithName();
		app.getControl().send("HELLO," + userName);
		this.setSize(600, 500);
		this.setResizable(true);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContactsList();
		this.setJMenuBar(createMenu());
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(createButtonPanel());
		this.add(createContactsPanel());

		// listener for playing a doubleclicked file in the JList
		this.contactsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					System.out.println(contactsList.getSelectedValue());
					// CALL, port, sender, receiver
					app.getControl().send(
							"CALL," + 5010 + "," + userName + ","
									+ contactsList.getSelectedValue());
				}
			}
		});
	}

	/**
	 * Set the title of the gui
	 */
	public void setGuiTitleWithName() {
		File file = new File("name.txt");
		if (!file.exists()) // check if the file already exist, else the
			new ChangeNameDialog(this);
		else
			try {
				FileReader fr = new FileReader("name.txt");
				BufferedReader br = new BufferedReader(fr);
				br = new BufferedReader(fr);
				String s = br.readLine();
				System.out.println("hello" + s);
				if (s == null)
					new ChangeNameDialog(this);
				else
					this.setUserName(s);

				this.setTitle("Audio conferencing tool"
						+ "                 Hello " + userName);

				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

	/**
	 * Read the "contact.txt" file to display all the saved contacts in the list
	 */
	public void setContactsList() {
		File file = new File("contacts.txt");
		if (!file.exists()) // check if the file already exist, else the
							// file is created
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		String s;
		try {
			FileReader fr = new FileReader("contacts.txt");
			BufferedReader br = new BufferedReader(fr);
			int i = 0;
			int j = 0;
			this.contacts = new ArrayList<Contact>();
			while ((s = br.readLine()) != null) {

				Contact contact = new Contact();
				contact.setName(s);
				contacts.add(contact);
				System.out.println(s);
				i++;
			}
			fr.close();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int k = 0; k < contacts.size(); k++) {
			Contact contact = (Contact) contacts.get(k);
		}
	}

	/**
	 * Refresh the JList with the saved contact list
	 */
	public void refreshJList() {
		model = new DefaultListModel();
		for (int i = 0; i < this.contacts.size(); i++) {
			Contact contact = (Contact) this.contacts.get(i);
			model.addElement(contact.getName());
		}
		this.contactsList.setModel(this.model);

	}

	/**
	 * Create the room panel in the main window, and initiate the JTree
	 * 
	 * @return the Roompanel, part of the contact panel in the main window
	 */
	private JPanel createRoomPanel() {

		JPanel panel = new JPanel();

		DefaultMutableTreeNode racine = new DefaultMutableTreeNode("All rooms");
		DefaultTreeModel treemodel = new DefaultTreeModel(racine);
		roomList = new JTree(treemodel);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane roomListPane = new JScrollPane(roomList);
		panel.add(new JLabel("Room list"));
		panel.add(roomListPane);

		return panel;
	}

	/**
	 * Create the part of the main window with all the contact informations
	 * 
	 * @return the contact panel in the main window
	 */
	private JPanel createContactsPanel() {

		JPanel panel = new JPanel();

		JPanel roomPanel = new JPanel();
		JPanel contactPanel = new JPanel();
		JPanel subContactPanel = new JPanel();

		newBtn = new JButton(newIcon);
		dltBtn = new JButton(dltIcon);

		roomPanel = createRoomPanel();

		model = new DefaultListModel();
		for (int i = 0; i < this.contacts.size(); i++) {
			Contact contact = (Contact) this.contacts.get(i);
			model.addElement(contact.getName());
		}
		this.contactsList = new JList(model);
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));

		subContactPanel.setLayout(new BoxLayout(subContactPanel,
				BoxLayout.X_AXIS));
		subContactPanel.add(new JLabel("Contact list"));
		subContactPanel.add(Box.createHorizontalGlue());
		newBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AddContactDialog(Gui.this);

			}
		});

		dltBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("delete " + contactsList.getSelectedIndex());
				Vector monVector = new Vector();
				File f = new File("contacts.txt");
				BufferedReader B = null;
				try {
					B = new BufferedReader(new FileReader(f));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				String ligne = "";
				try {
					ligne = B.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				while (ligne != null) {
					monVector.addElement(ligne);
					try {
						ligne = B.readLine();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				monVector.removeElementAt(contactsList.getSelectedIndex());
				PrintWriter P = null;
				try {
					P = new PrintWriter(new FileWriter(f));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i = 0; i < monVector.size(); i++) {
					P.println(monVector.get(i));
				}
				P.close();
				setContactsList();
				refreshJList();
			}
		});

		subContactPanel.add(newBtn);
		subContactPanel.add(dltBtn);

		contactPanel.add(subContactPanel);
		JScrollPane contactToCallScrollPane = new JScrollPane(this.contactsList);
		contactPanel.add(contactToCallScrollPane);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(roomPanel);
		panel.add(Box.createRigidArea(new Dimension(15, 0)));
		panel.add(contactPanel);

		return panel;
	}

	/**
	 * Create the part of the main window with the buttons
	 * 
	 * @return the panel with the buttons
	 */
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		callBtn = new JButton("Call contact", callIcon);
		hangUpBtn = new JButton("Hang up", hangIcon);
		callBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(contactsList.getSelectedValue());
				// CALL, port, sender, receiver
				app.getControl().send(
						"CALL," + 5010 + "," + userName + ","
								+ contactsList.getSelectedValue());
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(callBtn);
		panel.add(hangUpBtn);
		return panel;
	}

	/**
	 * Create the menu bar in the main window
	 * 
	 * @return the menu bar
	 */
	private JMenuBar createMenu() {
		this.menu = new JMenuBar();

		JMenu edit = new JMenu("Edit");
		JMenuItem setName = new JMenuItem("Change your name");
		setName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ChangeNameDialog(Gui.this);
			}
		});
		JMenuItem addContact = new JMenuItem("Add a contact");
		addContact.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddContactDialog(Gui.this);
			}
		});
		edit.add(addContact);
		edit.add(setName);

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

	public void acceptACall(String message, ControlChannel control) {

		Call call = Call.fromString(message);
		new AcceptACallDialog(control, call);
	}

	public void showMessage(String message) {

		JOptionPane.showMessageDialog(app.getGui(), message, null,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Dummy, create a tree, only for the test
	 * 
	 * @return a tree
	 */
	// private JTree buildTree() {
	// // Root creation
	// DefaultMutableTreeNode racine = new DefaultMutableTreeNode("All rooms");
	//
	// // Add leaves and way from the root
	// for (int i = 1; i < 6; i++) {
	// DefaultMutableTreeNode rep = new DefaultMutableTreeNode("Room" + i);
	//
	// // Add 4 ways
	// if (i < 4) {
	// DefaultMutableTreeNode rep2 = new DefaultMutableTreeNode(
	// "Contact" + i);
	// rep.add(rep2);
	// }
	// // Add leaves to the root
	// racine.add(rep);
	// }
	// // create tree
	// JTree arbre = new JTree(racine);
	// return arbre;
	// }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}