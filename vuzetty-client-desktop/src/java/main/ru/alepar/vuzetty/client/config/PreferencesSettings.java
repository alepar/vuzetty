package ru.alepar.vuzetty.client.config;

import java.util.prefs.Preferences;

public class PreferencesSettings implements Settings {

    private final Preferences preferences;

    public PreferencesSettings(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Integer getInteger(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getString(String key) {
        return preferences.get(key, null);
    }

}
