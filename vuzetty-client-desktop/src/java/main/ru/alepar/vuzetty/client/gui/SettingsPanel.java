package ru.alepar.vuzetty.client.gui;

import sun.awt.VerticalBagLayout;

import javax.swing.*;

public class SettingsPanel {

	private JPanel rootPanel;
	private JTextField nicknameField;
	private JTextField serverAddressHostField;
	private JTextField serverAddressPortField;
	private JLabel nicknameLabel;
	private JLabel serverAddressLabel;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final JFrame frame = new JFrame("Vuzetty settings");

		final SettingsPanel settings = new SettingsPanel();
		final SettingsButtons buttons = new SettingsButtons();

		final JPanel container = new JPanel(new VerticalBagLayout(5));
		container.add(settings.rootPanel);
		container.add(buttons.getRootPanel());

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(container);
		frame.pack();
		frame.setVisible(true);
	}
}
