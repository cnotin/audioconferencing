package se.ltu.M7017E.lab2.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.ltu.M7017E.lab2.client.ControlChannel;
import se.ltu.M7017E.lab2.common.messages.Call;

public class AcceptACallDialog extends JDialog {

	private ControlChannel control;
	private AcceptACallDialog me = this;

	public AcceptACallDialog(ControlChannel controlChannel, final Call call) {
		this.control = controlChannel;
		this.setSize(400, 115);
		this.setTitle(call.getSender() + " want to  discuss with you");
		this.setModal(true);

		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JLabel questionPanel = new JLabel("Do you want to accept this call ?");
		JButton yesButton = new JButton("Yes");
		JButton noButton = new JButton("No");

		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// save the contact
				System.out.println("yes");

				control.send("ANSWERCALL" + "," + call.getPort() + "," + 5012
						+ "," + call.getSender() + "," + call.getReceiver()
						+ ",yes");
				me.dispose();
			}
		});

		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// save the contact
				System.out.println("no");
				control.send("ANSWERCALL" + "," + 0 + "," + 0 + ","
						+ call.getSender() + "," + call.getReceiver() + ",no");

				me.dispose();
			}
		});

		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		mainPanel.add(questionPanel);
		mainPanel.add(buttonPanel);
		mainPanel.setVisible(true);
		this.add(mainPanel);
		this.setVisible(true);

	}
}
