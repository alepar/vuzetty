package ru.alepar.vuzetty.client.config;

import java.lang.reflect.Method;

public class PresenterSettings implements Settings {

	private final Settings currentSettings;
	private final Presenter.Factory factory;
	private final SettingsSaver saver;

    private Presenter presenter;

	public PresenterSettings(Settings currentSettings, Presenter.Factory factory, SettingsSaver saver) {
		this.currentSettings = currentSettings;
		this.factory = factory;
		this.saver = saver;
	}

    @Override
    public String getString(String key) {
        presenter = factory.create();
        try {
            highlightOnPresenter(key);
            populatePresenter();
            presenter.show();
            if(presenter.waitForOk()) {
                populateSaver();
                return getFromPresenter(key);
            } else {
                return currentSettings.getString(key);
            }
        } finally {
            presenter = null;
        }
    }

	private void populateSaver() {
		for (String key : presenter.knownKeys()) {
			final String oldValue = currentSettings.getString(key);
			final String newValue = getFromPresenter(key);

			if (oldValue == null || newValue == null || !newValue.equals(oldValue)) {
				if (oldValue != null || newValue != null) {
					saver.set(key, newValue);
				}
			}

		}
	}

	private void populatePresenter() {
		for (String key : presenter.knownKeys()) {
			final String value = currentSettings.getString(key);
			setOnPresenter(key, value);
		}
	}

	private String getFromPresenter(String key) {
		final String methodName = "get" + makeMethodNameFrom(key);
		try {
			final Method method = presenter.getClass().getDeclaredMethod(methodName);
			final Object result = method.invoke(presenter);
			return (String) result;
		} catch (Exception e) {
			throw new RuntimeException("failed to read from presenter " + key, e);
		}
	}

	private void highlightOnPresenter(String key) {
		final String methodName = "highlight" + makeMethodNameFrom(key);
		try {
			final Method method = presenter.getClass().getDeclaredMethod(methodName);
			method.invoke(presenter);
		} catch (Exception e) {
			throw new RuntimeException("failed to highlight " + key, e);
		}
	}

	private void setOnPresenter(String key, String value) {
		final String methodName = "set" + makeMethodNameFrom(key);
		try {
			final Method method = presenter.getClass().getDeclaredMethod(methodName, String.class);
			method.invoke(presenter, value);
		} catch (Exception e) {
			throw new RuntimeException("failed to present " + key, e);
		}
	}

	private static String makeMethodNameFrom(String key) {
		final String[] words = key.split("\\.");
		final StringBuilder sb = new StringBuilder(key.length());

		for (String word : words) {
			sb.append(word.toUpperCase().charAt(0));
			sb.append(word.substring(1));
		}

		return sb.toString();
	}

}
