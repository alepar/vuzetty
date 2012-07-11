package ru.alepar.vuzetty.client;

import ru.alepar.vuzetty.client.config.*;
import ru.alepar.vuzetty.client.gui.SettingsPanel;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ConfigurationFactory {

    static Configuration makeConfiguration() {
        return new SettingsConfiguration(makeCurrentSettings());
    }

    static void askForUserInputIfNeeded() {

    }

    public static SettingsPresenter createPresenter() {
        return new SettingsPresenter(
                makeCurrentSettings(),
                new SettingsPanel.Factory(),
                new PreferencesSettingsSaver(makePreferences())
        );
    }

    private static SettingsAggregator makeCurrentSettings() {

        final PreferencesSettings userSettings = new PreferencesSettings(makePreferences());
        final ResourceSettings builtinSettings = new ResourceSettings(ResourceBundle.getBundle("vuzetty-config"));

        final Settings[] existingSettings = new Settings[] {
                userSettings,
                builtinSettings,
        };
        return new SettingsAggregator(existingSettings);
    }

    private static Preferences makePreferences() {
        return Preferences.userNodeForPackage(ClientMain.class);
    }
}
