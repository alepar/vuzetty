package ru.alepar.vuzetty.client.config;

import java.util.prefs.Preferences;

public class PreferencesSettings implements Settings {

    private final Preferences preferences;

    public PreferencesSettings(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getString(String key) {
        return preferences.get(key, null);
    }

}
