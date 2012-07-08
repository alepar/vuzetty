package ru.alepar.vuzetty.client.config;

import java.util.ResourceBundle;

public class ResourceSettings implements Settings {

    private final ResourceBundle bundle;

    public ResourceSettings(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return null;
        }
    }
}
