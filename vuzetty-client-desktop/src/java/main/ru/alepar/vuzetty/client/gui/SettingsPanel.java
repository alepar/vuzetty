package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.Presenter;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel implements Presenter {

	public static final String[] KNOWN_KEYS = new String[]{
			"server.address.host",
			"server.address.port",
			"client.nickname",
	};

	private JPanel rootPanel;
	private JTextField nicknameField;
	private JTextField serverAddressHostField;
	private JTextField serverAddressPortField;
	private JLabel nicknameLabel;
	private JLabel serverAddressLabel;

	@Override
	public String[] knownKeys() {
		return KNOWN_KEYS;
	}

	@Override
	public void waitForOk() {
		throw new RuntimeException("alepar haven't implemented me yet");
	}

	@Override
	public void show() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		final JFrame frame = new JFrame("Vuzetty settings");

		final SettingsPanel settings = new SettingsPanel();
		final SettingsButtons buttons = new SettingsButtons();

		final JPanel container = new JPanel(new VerticalBagLayout());
		container.add(settings.rootPanel);
		container.add(buttons.getRootPanel());

		frame.setContentPane(container);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public String getServerAddressHost() {
		return serverAddressHostField.getText();
	}

	@Override
	public void setServerAddressHost(String value) {
		serverAddressHostField.setText(value);
	}

	@Override
	public void highlightServerAddressHost() {
		serverAddressLabel.setForeground(Color.RED);
	}

	@Override
	public String getServerAddressPort() {
		return serverAddressPortField.getText();
	}

	@Override
	public void setServerAddressPort(String value) {
		serverAddressPortField.setText(value);
	}

	@Override
	public void highlightServerAddressPort() {
		serverAddressLabel.setForeground(Color.RED);
	}

	@Override
	public String getClientNickname() {
		return nicknameField.getText();
	}

	@Override
	public void setClientNickname(String value) {
		nicknameField.setText(value);
	}

	@Override
	public void highlightClientNickname() {
		nicknameLabel.setForeground(Color.RED);
	}

	public static void main(String[] args) throws Exception {
		final SettingsPanel panel = new SettingsPanel();

		panel.show();

		while(true) {
			panel.highlightClientNickname();
		}

/*
		final JFrame frame = new JFrame();

		final JPanel panel = new JPanel();
		final JLabel label = new JLabel();
		label.setText("Hello, world!");
		panel.add(label);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		label.setForeground(Color.RED);
*/
	}

}
