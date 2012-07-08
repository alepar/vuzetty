package ru.alepar.vuzetty.client.config;

public class SettingsAggregator implements Settings {

    private final Settings[] settings;

    public SettingsAggregator(Settings[] settings) {
        this.settings = settings;
    }

    @Override
    public String getString(String key) {
        for (Settings setting : settings) {
            final String result = setting.getString(key);
            if(result != null) {
                return result;
            }
        }

        return null;
    }
}
