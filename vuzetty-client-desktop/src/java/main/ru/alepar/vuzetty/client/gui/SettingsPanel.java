package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.SettingsView;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel implements SettingsView {

	private JPanel rootPanel;
	private JLabel nicknameLabel;
	private JTextField nicknameField;
	private JTextField serverAddressHostField;
	private JTextField serverAddressPortField;
	private JLabel serverAddressLabel;

	private JFrame frame;
    private final SettingsButtons buttons;

    public SettingsPanel() {
		frame = new JFrame("Vuzetty settings");

        buttons = new SettingsButtons();
		final JPanel container = new JPanel(new VerticalBagLayout());

		container.add(rootPanel);
		container.add(buttons.getRootPanel());

		frame.setContentPane(container);
	}

    @Override
    public void setButtonListener(SettingsButtons.Listener listener) {
        buttons.setButtonListener(listener);
    }

    @Override
	public String[] knownKeys() {
		return new String[] {
				"client.nickname",
				"server.address.host",
				"server.address.port",
		};
	}

	@Override
	public void show() {
		frame.pack();
		frame.setVisible(true);
	}

    @Override
    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    @Override
	public String getServerAddressHost() {
		return trim(serverAddressHostField.getText());
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
		return trim(serverAddressPortField.getText());
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
		return trim(nicknameField.getText());
	}

	@Override
	public void setClientNickname(String value) {
		nicknameField.setText(value);
	}

	@Override
	public void highlightClientNickname() {
		highlight(nicknameLabel);
		frame.pack();
	}

    private static String trim(String text) {
        text = text.trim();
        if(text.isEmpty()) {
            text = null;
        }
        return text;
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final SettingsPanel panel = new SettingsPanel();

		panel.show();
		panel.highlightClientNickname();
	}

}
