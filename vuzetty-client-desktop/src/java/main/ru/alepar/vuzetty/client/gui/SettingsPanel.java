package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.Presenter;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class SettingsPanel implements Presenter {

	private JPanel rootPanel;
	private JLabel nicknameLabel;
	private JTextField nicknameField;
	private JTextField serverAddressHostField;
	private JTextField serverAddressPortField;
	private JLabel serverAddressLabel;

	private final CountDownLatch latch = new CountDownLatch(1);
	private JFrame frame;
	private boolean buttonResult;

	public SettingsPanel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}

		frame = new JFrame("Vuzetty settings");

		final SettingsButtons buttons = new SettingsButtons();
		final JPanel container = new JPanel(new VerticalBagLayout());

		buttons.setButtonListener(new SettingsButtons.Listener() {
			@Override
			public void onClick(boolean ok) {
				buttonResult = ok;
				latch.countDown();
			}
		});

		container.add(rootPanel);
		container.add(buttons.getRootPanel());

		frame.setContentPane(container);
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
	public boolean waitForOk() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		frame.setVisible(false);
		return buttonResult;
	}

	@Override
	public void show() {
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
		frame.pack();
	}

    public static class Factory implements Presenter.Factory {
        @Override
        public Presenter create() {
            return new SettingsPanel();
        }
    }

	public static void main(String[] args) throws Exception {
		final SettingsPanel panel = new SettingsPanel();

		panel.show();
		panel.highlightClientNickname();
	}
}
