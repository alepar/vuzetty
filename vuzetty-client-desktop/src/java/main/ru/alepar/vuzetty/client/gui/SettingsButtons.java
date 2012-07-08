package ru.alepar.vuzetty.client.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsButtons {
	private JPanel rootPanel;
	private JButton okButton;
	private JButton cancelButton;
	private Listener listener;

	public SettingsButtons() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onClick(true);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onClick(false);
			}
		});
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

	public void setButtonListener(Listener listener) {
		this.listener = listener;
	}

	public interface Listener {
		void onClick(boolean ok);
	}
}
