package se.ltu.M7017E.lab2.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
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
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import se.ltu.M7017E.lab2.client.App;
import se.ltu.M7017E.lab2.client.Room;
import se.ltu.M7017E.lab2.messages.Call;

@Getter
public class Gui extends JFrame {

	private static final long serialVersionUID = -9219551892569083659L;
	private JMenuBar menu;
	private JButton callBtn;
	private JButton hangUpBtn;
	private JButton joinBtn;
	private JButton newBtn;
	private JButton deleteBtn;
	private JButton quitBtn;
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
	private ImageIcon quitIcon = new ImageIcon(getClass().getResource(
			"/icons/quit_button.png"));

	private DefaultListModel contactsListModel = new DefaultListModel();
	private DefaultTreeModel roomsTreeModel;
	private App app;

	/**
	 * Interface of the application. Display the main window and set the tree.
	 */
	public Gui(final App app) {
		this.app = app;
		JPanel subPanel = new JPanel();

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
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
		subPanel.add(createRoomPanel());
		subPanel.add(createContactsPanel());
		this.add(createButtonPanel());
		this.add(subPanel);

		// listener for playing a doubleclicked file in the JList
		this.contactsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					callContact();
				}
			}
		});
		app.createAllRoomList();
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
		roomsTreeModel = new DefaultTreeModel(racine);
		roomList = new JTree(roomsTreeModel);
		roomList.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		roomList.expandRow(2);
		roomList.setCellRenderer(new CellRender(roomList.getCellRenderer()));
		roomList.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) roomList
						.getLastSelectedPathComponent();
				if (nodeSelected == null) {
				} else if ((nodeSelected.getLevel() == 1)
						&& app.getMyRooms().contains(
								nodeSelected.getUserObject())) {
					// if the room is already joined, display quitBtn
					joinBtn.setVisible(false);
					quitBtn.setVisible(true);
				} else if ((nodeSelected.getLevel() == 2)
						&& app.getMyRooms().contains(
								((DefaultMutableTreeNode) nodeSelected
										.getParent()).getUserObject())) {
					// if the room is already joined, display quitBtn
					joinBtn.setVisible(false);
					quitBtn.setVisible(true);
				} else {
					// otherwise display JoinBtn
					joinBtn.setVisible(true);
					quitBtn.setVisible(false);
				}
			}
		});

		roomList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					System.out.println("the game");
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) roomList
							.getLastSelectedPathComponent();
					if (node == null) {
						displayRoomList(app.getAllRooms());
					} else if (node.getLevel() == 0) {
						// the selection is the Root
						showMessage("No Room selected");
					} else if (node.getLevel() == 1) {
						// the selection is a room, no node change
						app.joinRoom(((Room) node.getUserObject()).getId());
					} else if (node.getLevel() == 2) {
						// the selection is a name in a Room, get the room
						node = (DefaultMutableTreeNode) node.getParent();
						app.joinRoom(((Room) node.getUserObject()).getId());
					}
					app.createMyRooms(app.getAllRooms());
					displayRoomList(app.getAllRooms());
				}
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane roomListPane = new JScrollPane(roomList);
		subPanel.add(new JLabel("Room list"));
		subPanel.add(Box.createHorizontalGlue());
		panel.setSize(200, 300);
		panel.add(subPanel);
		panel.add(roomListPane);

		return panel;
	}

	@AllArgsConstructor
	private class CellRender implements TreeCellRenderer {
		TreeCellRenderer originalRender;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			JLabel label = (JLabel) originalRender
					.getTreeCellRendererComponent(tree, value, selected,
							expanded, leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getLevel() == 1) {
				Room room = (Room) node.getUserObject();
				if (app.getMyRooms().contains(room)) {
					label.setText("<html><strong> Room " + room.getId()
							+ "</html></strong>");
				} else {
					label.setText("Room " + room.getId());
				}
			}
			return label;
		}
	}

	/**
	 * Create the part of the main window with all the contact informations
	 * 
	 * @return the contact panel in the main window
	 */
	private JPanel createContactsPanel() {

		JPanel panel = new JPanel();

		JPanel contactPanel = new JPanel();
		JPanel subContactPanel = new JPanel();

		newBtn = new JButton(newIcon);
		deleteBtn = new JButton(deleteIcon);

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
				if (name != null && !name.isEmpty()) {
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
		panel.add(Box.createRigidArea(new Dimension(15, 0)));
		panel.add(contactPanel);

		return panel;
	}

	/**
	 * Create the top part of the main window with the buttons
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
				if (node == null) {
					displayRoomList(app.getAllRooms());
				} else if (node.getLevel() == 0) {
					// the selection is the Root
					showMessage("No Room selected");
				} else if (node.getLevel() == 1) {
					// the selection is a room, no node change
					app.joinRoom(((Room) node.getUserObject()).getId());
				} else if (node.getLevel() == 2) {
					// the selection is a name in a Room, get the room
					node = (DefaultMutableTreeNode) node.getParent();
					app.joinRoom(((Room) node.getUserObject()).getId());
				}
				app.createMyRooms(app.getAllRooms());
				displayRoomList(app.getAllRooms());
			}
		});

		quitBtn = new JButton("Quit Room", quitIcon);
		quitBtn.setVisible(false);
		quitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) roomList
						.getLastSelectedPathComponent();
				// if (node.toString().contains(()))
				if (node == null) {
					displayRoomList(app.getAllRooms());
				} else if (node.getLevel() == 0) {
					// the selection is the Root
					showMessage("No Room selected");
				} else if (node.getLevel() == 1) {
					// the selection is a room, no node change
					app.leaveRoom(((Room) node.getUserObject()).getId());
				} else if (node.getLevel() == 2) {
					// the selection is a name in a Room, get the room
					node = (DefaultMutableTreeNode) node.getParent();
					app.leaveRoom(((Room) node.getUserObject()).getId());
				}
				app.createMyRooms(app.getAllRooms());
				displayRoomList(app.getAllRooms());
			}
		});

		callBtn = new JButton("Call contact", callIcon);
		hangUpBtn = new JButton("Hang up", hangIcon);
		hangUpBtn.setVisible(false);
		callBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				callContact();
			}
		});
		hangUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.askToStopCall();
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
		panel.add(joinBtn);
		panel.add(quitBtn);
		panel.add(Box.createHorizontalGlue());
		panel.add(callBtn);
		panel.add(hangUpBtn);
		return panel;
	}

	/**
	 * Ask to call a contact.
	 */
	private void callContact() {
		new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				System.out.println(contactsList.getSelectedValue());

				if (contactsList.getSelectedValue() != null) {
					app.askToCall((String) contactsList.getSelectedValue());
				} else {
					showMessage("Please select a person to call!");
				}
				return null;
			}
		}.execute();
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

	/**
	 * 
	 * @param message
	 * @param app
	 */
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

	/**
	 * Show a message to the user
	 * 
	 */

	public void showMessage(String message) {

		JOptionPane.showMessageDialog(app.getGui(), message, null,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Display a list of rooms in the tree in the main interface
	 * 
	 * @param roomListToDisplay
	 *            The list of rooms to display in the tree
	 */
	/*
	 * public void displayRoomList(List<Room> roomListToDisplay) {
	 * DefaultMutableTreeNode root = (DefaultMutableTreeNode) roomsTreeModel
	 * .getRoot();
	 * 
	 * // clear the modeltree if there is already something
	 * root.removeAllChildren(); for (Room room : roomListToDisplay) { // if
	 * (!room.getAudience().isEmpty()) { DefaultMutableTreeNode newRoom = new
	 * DefaultMutableTreeNode(room); for (String contactName :
	 * room.getAudience()) { MutableTreeNode contact = new
	 * DefaultMutableTreeNode( contactName); newRoom.add(contact); }
	 * root.add(newRoom); // } } // To update the tree roomsTreeModel.reload();
	 * roomList.setModel(roomsTreeModel); }
	 */
	public void displayRoomList(List<Room> roomListToDisplay) {
		Set<Integer> indexRoom = new TreeSet<Integer>();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) roomsTreeModel
				.getRoot();

		// clear the modeltree if there is already something
		root.removeAllChildren();
		indexRoom.clear();
		for (Room room : roomListToDisplay) {
			if (!room.getAudience().isEmpty()) {
				indexRoom.add(room.getId());
				DefaultMutableTreeNode newRoom = new DefaultMutableTreeNode(
						room);
				for (String contactName : room.getAudience()) {
					MutableTreeNode contact = new DefaultMutableTreeNode(
							contactName);
					newRoom.add(contact);
				}
				root.add(newRoom);
			}
		}
		for (int i = 1; i < 255; i++) {
			if (!indexRoom.contains(i)) {
				DefaultMutableTreeNode emptyRoom;
				emptyRoom = new DefaultMutableTreeNode(new Room(i));
				root.add(emptyRoom);
			}
		}
		// To update the tree
		roomsTreeModel.reload();
		roomList.setModel(roomsTreeModel);
	}
}
