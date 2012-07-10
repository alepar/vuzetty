package ru.alepar.vuzetty.client.config;

import ru.alepar.vuzetty.client.gui.SettingsButtons;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class SettingsPresenter implements Settings {

	private final Settings currentSettings;
	private final SettingsView.Factory factory;
	private final SettingsSaver saver;

    private SettingsView view;
    private boolean okPressed;

	public SettingsPresenter(Settings currentSettings, SettingsView.Factory factory, SettingsSaver saver) {
		this.currentSettings = currentSettings;
		this.factory = factory;
		this.saver = saver;
	}

    @Override
    public String getString(String key) {
        view = factory.create();
        try {
            highlightOnPresenter(key);
            populatePresenter();
            view.show();

            final CountDownLatch latch = new CountDownLatch(1);
            view.setButtonListener(new SettingsButtons.Listener() {
                @Override
                public void onClick(boolean ok) {
                    okPressed = ok;
                    latch.countDown();
                }
            });
            latch.await();

            if(okPressed) {
                populateSaver();
                return getFromPresenter(key);
            } else {
                return currentSettings.getString(key);
            }
        } catch(InterruptedException e) {
            throw new RuntimeException("interrupted while waiting for user input", e);
        } finally {
            view.close();
            view = null;
        }
    }

    public void show() {
        view = factory.create();
        populatePresenter();
        view.show();

        view.setButtonListener(new SettingsButtons.Listener() {
            @Override
            public void onClick(boolean ok) {
                view.close();
                if(ok) {
                    populateSaver();
                }
            }
        });
    }

	private void populateSaver() {
		for (String key : view.knownKeys()) {
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
		for (String key : view.knownKeys()) {
			final String value = currentSettings.getString(key);
			setOnPresenter(key, value);
		}
	}

	private String getFromPresenter(String key) {
		final String methodName = "get" + makeMethodNameFrom(key);
		try {
			final Method method = view.getClass().getDeclaredMethod(methodName);
			final Object result = method.invoke(view);
			return (String) result;
		} catch (Exception e) {
			throw new RuntimeException("failed to read from view " + key, e);
		}
	}

	private void highlightOnPresenter(String key) {
		final String methodName = "highlight" + makeMethodNameFrom(key);
		try {
			final Method method = view.getClass().getDeclaredMethod(methodName);
			method.invoke(view);
		} catch (Exception e) {
			throw new RuntimeException("failed to highlight " + key, e);
		}
	}

	private void setOnPresenter(String key, String value) {
		final String methodName = "set" + makeMethodNameFrom(key);
		try {
			final Method method = view.getClass().getDeclaredMethod(methodName, String.class);
			method.invoke(view, value);
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
