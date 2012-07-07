package ru.alepar.vuzetty.client.config;

import java.lang.reflect.Method;

public class PresenterSettings implements Settings {

	private final Settings currentSettings;
	private final Presenter presenter;
	private final SettingsSaver saver;

	public PresenterSettings(Settings currentSettings, Presenter presenter, SettingsSaver saver) {
		this.currentSettings = currentSettings;
		this.presenter = presenter;
		this.saver = saver;
	}

	@Override
    public Integer getInteger(String key) {
        throw new RuntimeException("alepar haven't implemented me yet");
    }

    @Override
    public String getString(String key) {
		final String currentValue = currentSettings.getString(key);
		highlightOnPresenter(key);
		setOnPresenter(key, currentValue);
		presenter.show();
		presenter.waitForOk();
		final String newValue = getFromPresenter(key);
		saver.set(key, newValue);
		return newValue;
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
