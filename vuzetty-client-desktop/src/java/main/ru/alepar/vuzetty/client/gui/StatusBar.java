package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.ConfigurationFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusBar {
    private JPanel rootPanel;
    private JButton settingsButton;

    public StatusBar() {
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationFactory.createPresenter().show();
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
