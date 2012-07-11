package ru.alepar.vuzetty.client.config;

import ru.alepar.vuzetty.client.ClientMain;
import ru.alepar.vuzetty.client.gui.SettingsPanel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;

public class ConfigurationFactory {

    public static final Set<String> MANDATORY_SETTINGS = new HashSet<String>(Arrays.asList(
            "client.nickname",
            "server.address.host",
            "server.address.port"
    ));

    public static Configuration makeConfiguration() {
        return new SettingsConfiguration(makeCurrentSettings());
    }

    public static void askForUserInputIfNeeded() {
        final Set<String> missingSettings = missingSettings();
        if(!missingSettings.isEmpty()) {
            final SettingsPresenter presenter = createPresenter();
            final CountDownLatch latch = new CountDownLatch(1);
            presenter.setCloseListener(new SettingsPresenter.CloseListener() {
                @Override
                public void onClose() {
                    latch.countDown();
                }
            });
            presenter.highlightKeys(missingSettings);
            presenter.show();
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted while waiting for user input", e);
            }
        }
    }

	public static SettingsPresenter createPresenter() {
		final Settings persistedSettings = makeCurrentSettings();
		final Settings prefilledSettings = new SettingsAggregator(new Settings[] {
				persistedSettings,
				new GeneratedSettings()
		});
		return new SettingsPresenter(
				prefilledSettings,
				persistedSettings,
				new SettingsPanel(),
				new PreferencesSettingsSaver(makePreferences())
		);
	}

    private static Set<String> missingSettings() {
        final Set<String> missing = new HashSet<String>();
        final Settings settings = makeCurrentSettings();
        for (String key : MANDATORY_SETTINGS) {
            if(settings.getString(key) == null) {
                missing.add(key);
            }
        }

        return missing;
    }

    private static Settings makeCurrentSettings() {
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
