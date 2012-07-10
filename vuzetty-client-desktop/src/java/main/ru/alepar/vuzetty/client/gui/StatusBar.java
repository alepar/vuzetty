package ru.alepar.vuzetty.client.gui;

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
                throw new RuntimeException("parfenal, implement me!");
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
