package ru.alepar.vuzetty.client.config;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.alepar.vuzetty.client.gui.SettingsButtons;

@RunWith(JMock.class)
public class SettingsPresenterTest {

	private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void populatesViewWithCurrentSettingsAndDisplaysItViaPresenterAndSavesIt() throws Exception {
		final String KEY = "server.address.host";
		final String OLD_VALUE = "value";
		final String NEW_VALUE = "new value";

		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final SettingsView view = mockery.mock(SettingsView.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(currentSettings).getString(KEY);
				will(returnValue(OLD_VALUE));

			allowing(view).knownKeys();
				will(returnValue(new String[] {KEY}));

			one(view).setServerAddressHost(OLD_VALUE);
			one(view).setButtonListener(with(any(SettingsButtons.Listener.class)));
			one(view).show();
			one(view).close();

			allowing(view).getServerAddressHost();
				will(returnValue(NEW_VALUE));

			one(saver).set(KEY, NEW_VALUE);
		}});

        final SettingsPresenter presenterSettings = new SettingsPresenter(currentSettings, currentSettings, view, saver);
		presenterSettings.show();
		presenterSettings.onViewClosed(true);
    }

	@Test
	public void populatesViewWithAllKnownKeysAndSavesOnlyChangedValues() throws Exception {
		final String[] KNOWN_KEYS = new String[] {
				"server.address.host",
				"server.address.port",
				"client.nickname"
		};

		final String OLD_VALUE = "old_value";
		final String NEW_VALUE = "new_value";

		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final SettingsView view = mockery.mock(SettingsView.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(view).knownKeys();
				will(returnValue(KNOWN_KEYS));

			allowing(currentSettings).getString(KNOWN_KEYS[0]);
				will(returnValue(OLD_VALUE));
			allowing(currentSettings).getString(KNOWN_KEYS[1]);
				will(returnValue(OLD_VALUE));
			allowing(currentSettings).getString(KNOWN_KEYS[2]);
				will(returnValue(OLD_VALUE));

			one(view).setServerAddressHost(OLD_VALUE);
			one(view).setServerAddressPort(OLD_VALUE);
			one(view).setClientNickname(OLD_VALUE);

			one(view).setButtonListener(with(any(SettingsButtons.Listener.class)));
			one(view).show();
			one(view).close();

			allowing(view).getServerAddressHost();
				will(returnValue(NEW_VALUE));
			one(view).getServerAddressPort();
				will(returnValue(OLD_VALUE));
			one(view).getClientNickname();
				will(returnValue(NEW_VALUE));

			one(saver).set(KNOWN_KEYS[0], NEW_VALUE);
			one(saver).set(KNOWN_KEYS[2], NEW_VALUE);
		}});

        final SettingsPresenter presenterSettings = new SettingsPresenter(currentSettings, currentSettings, view, saver);
		presenterSettings.show();
		presenterSettings.onViewClosed(true);
	}

	@Test
	public void handlesNullsInValuesCorrectly() throws Exception {
		final String[] KNOWN_KEYS = new String[] {
				"server.address.host",
				"server.address.port",
				"client.nickname"
		};

		final String NOT_NULL = "NOT_NULL";

		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final SettingsView view = mockery.mock(SettingsView.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(view).knownKeys();
				will(returnValue(KNOWN_KEYS));

			allowing(currentSettings).getString(KNOWN_KEYS[0]);
				will(returnValue(null));
			allowing(currentSettings).getString(KNOWN_KEYS[1]);
				will(returnValue(null));
			allowing(currentSettings).getString(KNOWN_KEYS[2]);
				will(returnValue(NOT_NULL));

			one(view).setServerAddressHost(null);
			one(view).setServerAddressPort(null);
			one(view).setClientNickname(NOT_NULL);

			one(view).setButtonListener(with(any(SettingsButtons.Listener.class)));
			one(view).show();
			one(view).close();

			allowing(view).getServerAddressHost();
				will(returnValue(NOT_NULL));
			one(view).getServerAddressPort();
				will(returnValue(null));
			one(view).getClientNickname();
				will(returnValue(null));

			one(saver).set(KNOWN_KEYS[0], NOT_NULL);
			one(saver).set(KNOWN_KEYS[2], null);
		}});

        final SettingsPresenter presenterSettings = new SettingsPresenter(currentSettings, currentSettings, view, saver);
		presenterSettings.show();
		presenterSettings.onViewClosed(true);
	}

}
