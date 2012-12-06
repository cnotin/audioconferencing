package se.ltu.M7017E.lab2.client.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import se.ltu.M7017E.lab2.client.App;
import se.ltu.M7017E.lab2.common.Room;
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

	private DefaultListModel contactsListModel = new DefaultListModel();
	private DefaultTreeModel modeltree;
	private App app;
	private int roomSelected = 1000;

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
		app.createAllRoomList();
		// app.createMyRoomsList();
		displayRoomList(app.getAllRooms());
	}

	public class RefreshContactsListRunnable implements Runnable {
		@Override
		public void run() {
			contactsListModel.clear();

			for (String contact : app.getContacts()) {
				if (app.getConnected().contains(contact)) {
					contactsListModel.addElement(contact);
				} else {
					contactsListModel.addElement(contact + " (Disconnected)");
				}
			}
		}
	}

	public void refreshContactsList() {
		SwingUtilities.invokeLater(new RefreshContactsListRunnable());
	}

	/**
	 * Create the room panel in the main window, and initiate the JTree
	 * 
	 * @return the Roompanel, part of the contact panel in the main window
	 */
	private JPanel createRoomPanel() {

		JPanel panel = new JPanel();
		JPanel subPanel = new JPanel();

		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

		// initialize the RoomList as a treemodel
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode("All rooms");
		modeltree = new DefaultTreeModel(racine);
		roomList = new JTree(modeltree);
		roomList.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		roomList.expandRow(1);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane roomListPane = new JScrollPane(roomList);
		subPanel.add(new JLabel("Room list"));
		subPanel.add(Box.createHorizontalGlue());
		panel.setSize(200, 300);
		panel.add(subPanel);
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
		this.contactsList = new JList(contactsListModel);
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
		joinBtn.setVisible(true);
		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) roomList
						.getLastSelectedPathComponent();
				// the selection is the Root
				if (node.getLevel() == 0) {
					showMessage("No Room selected");
				}
				// the selection is a room
				else if (node.getLevel() == 1) {
					app.joinRoom(((Room) node.getUserObject()).getId());
				}
				// the selection is a name in a Room
				else {
					DefaultMutableTreeNode parentnode = new DefaultMutableTreeNode();
					parentnode = (DefaultMutableTreeNode) node.getParent();
					app.joinRoom(((Room) parentnode.getUserObject()).getId());
				}
				// app.createAllRoomList();
				displayRoomList(app.getAllRooms());
			}
		});
		callBtn = new JButton("Call contact", callIcon);
		hangUpBtn = new JButton("Hang up", hangIcon);
		callBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				callContact();
			}
		});
		hangUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.stopCall();
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
			this.app.askToCall((String) contactsList.getSelectedValue());
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

	public void acceptACall(String message, App app) {
		Call call = Call.fromString(message);
		int ret = JOptionPane.showConfirmDialog(this,
				"Do you accept to talk with " + call.getSender(),
				"Incoming call", JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			app.answerCall("yes", call);
			app.call(call.getIpSender(), call.getPortSender());
		} else {
			app.answerCall("no", call);
		}
	}

	public void showMessage(String message) {

		JOptionPane.showMessageDialog(app.getGui(), message, null,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Display the room List in the RoomPanel
	 * 
	 * use "nc localhost 4000" in a terminal to create a server and test
	 */
	private void displayRoomList(List<Room> roomListToDisplay) {
		// clear the modeltree if there is something
		((DefaultMutableTreeNode) modeltree.getRoot()).removeAllChildren();
		for (Room room : roomListToDisplay) {
			MutableTreeNode newRoom = new DefaultMutableTreeNode(room);
			for (int j = 0; j < room.getAudience().size(); j++) {
				MutableTreeNode contact = new DefaultMutableTreeNode(room
						.getAudienceAsStrings().get(j));
				newRoom.insert(contact, j);
			}
			((DefaultMutableTreeNode) modeltree.getRoot()).add(newRoom);
		}
		// To update the tree
		modeltree.reload();
		roomList.setModel(modeltree);
	}

	public int getRoomSelected() {
		return roomSelected;
	}
}
