package ru.alepar.vuzetty.client.config;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesSettingsSaver implements SettingsSaver {

	private final Preferences preferences;

	public PreferencesSettingsSaver(Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public void set(String key, String value) {
		preferences.put(key, value);
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			throw new RuntimeException("failed to save user settings", e);
		}
	}
}
