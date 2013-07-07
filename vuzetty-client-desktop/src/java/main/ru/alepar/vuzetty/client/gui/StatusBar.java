package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.config.ConfigurationFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusBar {

    private JPanel rootPanel;
    private JButton settingsButton;
    private JButton ownTorrentsButton;
    private JLabel upnpCountLabel;

    private final OwnTorrentsClickListener ownTorrentsClickListener;

    private boolean ownTorrentsPressed;

    public StatusBar(boolean showOwnTorrents, OwnTorrentsClickListener ownTorrentsClickListener) {
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationFactory.createPresenter().show();
            }
        });

        ownTorrentsPressed = showOwnTorrents;
        ownTorrentsButton.setSelected(ownTorrentsPressed);
        ownTorrentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ownTorrentsPressed = !ownTorrentsPressed;
                ownTorrentsButton.setSelected(ownTorrentsPressed);

                if(StatusBar.this.ownTorrentsClickListener != null) {
                    StatusBar.this.ownTorrentsClickListener.onClick(ownTorrentsPressed);
                }
            }
        });

        this.ownTorrentsClickListener = ownTorrentsClickListener;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setUpnpCount(int count) {
        upnpCountLabel.setText("" + count);
    }

    public interface OwnTorrentsClickListener {
        void onClick(boolean pressed);
    }

}
