package se.ltu.M7017E.lab2.client.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	private JButton joinBtn;
	private JButton newBtn;
	private JButton deleteBtn;
	private JList contactsList;
	public JTree roomList;
	private ImageIcon callIcon = new ImageIcon(getClass().getResource(
			"/icons/call_button.png"));
	private ImageIcon hangIcon = new ImageIcon(getClass().getResource(
			"/icons/hang_button.png"));
	private ImageIcon newIcon = new ImageIcon(getClass().getResource(
			"/icons/new_button.png"));
	private ImageIcon deleteIcon = new ImageIcon(getClass().getResource(
			"/icons/dlt_button.png"));
	private ImageIcon joinIcon = new ImageIcon(getClass().getResource(
			"/icons/door_button.png"));

	private DefaultListModel model = new DefaultListModel();
	private App app;

	/**
	 * Interface of the application. Display the main window
	 */
	public Gui(final App app) {
		this.app = app;

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
		this.setTitle("Audio conferencing tool. Hello " + app.getUsername());
		this.setSize(600, 500);
		this.setResizable(true);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setJMenuBar(createMenu());
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(createButtonPanel());
		this.add(createContactsPanel());

		// listener for playing a doubleclicked file in the JList
		this.contactsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					callContact();
				}
			}
		});
	}

	/**
	 * Refresh the JList with the saved contact list
	 */
	public void refreshContactsList() {
		model.clear();
		for (Contact contact : app.getContacts()) {
			model.addElement(contact.getName());
		}
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
		deleteBtn = new JButton(deleteIcon);

		roomPanel = createRoomPanel();

		refreshContactsList();
		this.contactsList = new JList(model);
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));

		subContactPanel.setLayout(new BoxLayout(subContactPanel,
				BoxLayout.X_AXIS));
		subContactPanel.add(new JLabel("Contact list"));
		subContactPanel.add(Box.createHorizontalGlue());
		newBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(null,
						"Choose a name", "Name selection",
						JOptionPane.QUESTION_MESSAGE);
				if (name != null) {
					app.addContact(name);
					refreshContactsList();
				}
			}
		});

		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.removeContact((String) contactsList.getSelectedValue());
				refreshContactsList();
			}
		});

		subContactPanel.add(newBtn);
		subContactPanel.add(deleteBtn);

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
		joinBtn = new JButton("Join room", joinIcon);
		callBtn = new JButton("Call contact", callIcon);
		hangUpBtn = new JButton("Hang up", hangIcon);
		callBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				callContact();
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(joinBtn);
		panel.add(callBtn);
		panel.add(hangUpBtn);
		return panel;
	}

	private void callContact() {

		System.out.println(contactsList.getSelectedValue());

		if (contactsList.getSelectedValue() != null) {
			// TODO use app's method to do this. Don't create a fucking message
			// in this GUI.
		} else {
			showMessage("Please select a person to call!");
		}
	}

	/**
	 * Create the menu bar in the main window
	 * 
	 * @return the menu bar
	 */
	private JMenuBar createMenu() {
		this.menu = new JMenuBar();

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

}