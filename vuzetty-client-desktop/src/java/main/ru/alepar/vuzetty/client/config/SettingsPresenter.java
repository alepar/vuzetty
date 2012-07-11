package ru.alepar.vuzetty.client.config;

import ru.alepar.vuzetty.client.gui.SettingsButtons;

import java.lang.reflect.Method;
import java.util.Set;

public class SettingsPresenter {

    private final Settings prefilledSettings;
    private final Settings persistedSettings;
	private final SettingsView view;
	private final SettingsSaver saver;

    private CloseListener closeListener;


    public SettingsPresenter(Settings prefilledSettings, Settings persistedSettings, SettingsView view, SettingsSaver saver) {
        this.prefilledSettings = prefilledSettings;
        this.persistedSettings = persistedSettings;
		this.view = view;
		this.saver = saver;
	}

    public void show() {
        populateView();
        view.show();

        view.setButtonListener(new SettingsButtons.Listener() {
            @Override
            public void onClick(boolean ok) {
                view.close();
                if(ok) {
                    populateSaver();
                }
                if(closeListener != null) {
                    closeListener.onClose();
                }
            }
        });
    }

    public void setCloseListener(CloseListener listener) {
        this.closeListener = listener;
    }

    public void highlightKeys(Set<String> keys) {
        for (String key : keys) {
            highlightOnView(key);
        }
    }

    private void populateSaver() {
		for (String key : view.knownKeys()) {
			final String oldValue = persistedSettings.getString(key);
			final String newValue = getFromView(key);

			if (oldValue == null || newValue == null || !newValue.equals(oldValue)) {
				if (oldValue != null || newValue != null) {
					saver.set(key, newValue);
				}
			}
		}
	}

	private void populateView() {
		for (String key : view.knownKeys()) {
			final String value = prefilledSettings.getString(key);
			setOnView(key, value);
		}
	}

	private String getFromView(String key) {
		final String methodName = "get" + makeMethodNameFrom(key);
		try {
			final Method method = view.getClass().getDeclaredMethod(methodName);
			final Object result = method.invoke(view);
			return (String) result;
		} catch (Exception e) {
			throw new RuntimeException("failed to read from view " + key, e);
		}
	}

	private void highlightOnView(String key) {
		final String methodName = "highlight" + makeMethodNameFrom(key);
		try {
			final Method method = view.getClass().getDeclaredMethod(methodName);
			method.invoke(view);
		} catch (Exception e) {
			throw new RuntimeException("failed to highlight " + key, e);
		}
	}

	private void setOnView(String key, String value) {
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

    public interface CloseListener {
        void onClose();
    }
}
