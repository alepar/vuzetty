package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.ClientMain;
import ru.alepar.vuzetty.client.config.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class StatusBar {
    private JPanel rootPanel;
    private JButton settingsButton;

    public StatusBar() {
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Preferences preferences = Preferences.userNodeForPackage(ClientMain.class);

                final PreferencesSettings userSettings = new PreferencesSettings(preferences);
                final ResourceSettings builtinSettings = new ResourceSettings(ResourceBundle.getBundle("vuzetty-config"));

                final Settings[] existingSettings = new Settings[] {
                        userSettings,
                        builtinSettings,
                };

                new SettingsPresenter(
                        new SettingsAggregator(existingSettings),
                        new SettingsPanel.Factory(),
                        new PreferencesSettingsSaver(preferences)
                ).show();
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
