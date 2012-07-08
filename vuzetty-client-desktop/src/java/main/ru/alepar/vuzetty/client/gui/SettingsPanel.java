package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.Presenter;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel implements Presenter {

	private JPanel rootPanel;
	private JLabel nicknameLabel;
	private JTextField nicknameField;
	private JTextField serverAddressHostField;
	private JTextField serverAddressPortField;
	private JLabel serverAddressLabel;

	@Override
	public String[] knownKeys() {
		return new String[] {
				"client.nickname",
				"server.address.host",
				"server.address.port",
		};
	}

	@Override
	public void waitForOk() {
		throw new RuntimeException("alepar haven't implemented me yet");
	}

	@Override
	public void show() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}

		final JFrame frame = new JFrame("Vuzetty settings");
		final SettingsButtons buttons = new SettingsButtons();
		final JPanel container = new JPanel(new VerticalBagLayout());

		container.add(rootPanel);
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
		highlight(serverAddressLabel);
	}

	private void highlight(JLabel component) {
		component.setForeground(Color.RED);
		component.setFont(component.getFont().deriveFont(Font.BOLD));
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
		highlightServerAddressHost();
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
		highlight(nicknameLabel);
	}

	public static void main(String[] args) throws Exception {
		final SettingsPanel panel = new SettingsPanel();

		panel.show();
		panel.highlightClientNickname();
	}
}
