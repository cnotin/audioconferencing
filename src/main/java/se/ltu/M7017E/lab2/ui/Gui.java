package se.ltu.M7017E.lab2.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
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
		callBtn = new JButton("Call");
		hangUpBtn = new JButton("Hang up");
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(callBtn);
		panel.add(hangUpBtn);
		return panel;
	}

	private JMenuBar createMenu() {
		this.menu = new JMenuBar();

		JMenu edit = new JMenu("Edit");
		JMenuItem preferences = new JMenuItem("Preferences");
		preferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		edit.add(preferences);
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