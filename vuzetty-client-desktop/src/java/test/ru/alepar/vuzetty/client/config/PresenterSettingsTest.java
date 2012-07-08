package ru.alepar.vuzetty.client.config;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class PresenterSettingsTest {

	private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void asksCurrentSettingsIfValueExistsAndHighlightsItAndDisplaysItViaPresenter() throws Exception {
		final String KEY = "server.address.host";
		final String OLD_VALUE = "value";
		final String NEW_VALUE = "new value";

		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final Presenter presenter = mockery.mock(Presenter.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(currentSettings).getString(KEY);
				will(returnValue(OLD_VALUE));

			allowing(presenter).knownKeys();
				will(returnValue(new String[] {KEY}));

			one(presenter).highlightServerAddressHost();
			one(presenter).setServerAddressHost(OLD_VALUE);

			one(presenter).show();
			one(presenter).waitForOk(); will(returnValue(true));

			allowing(presenter).getServerAddressHost();
				will(returnValue(NEW_VALUE));

			one(saver).set(KEY, NEW_VALUE);
		}});

		final Settings presenterSettings = new PresenterSettings(currentSettings, presenter, saver);
		assertThat(presenterSettings.getString(KEY), equalTo(NEW_VALUE));
    }

	@Test
	public void populatesAllKnownKeysInPresenterAndSavesOnlyChangedValues() throws Exception {
		final String[] KNOWN_KEYS = new String[] {
				"server.address.host",
				"server.address.port",
				"client.nickname"
		};

		final String OLD_VALUE = "old_value";
		final String NEW_VALUE = "new_value";

		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final Presenter presenter = mockery.mock(Presenter.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(presenter).knownKeys();
				will(returnValue(KNOWN_KEYS));

			allowing(currentSettings).getString(KNOWN_KEYS[0]);
				will(returnValue(OLD_VALUE));
			allowing(currentSettings).getString(KNOWN_KEYS[1]);
				will(returnValue(OLD_VALUE));
			allowing(currentSettings).getString(KNOWN_KEYS[2]);
				will(returnValue(OLD_VALUE));

			one(presenter).highlightServerAddressHost();
			one(presenter).setServerAddressHost(OLD_VALUE);
			one(presenter).setServerAddressPort(OLD_VALUE);
			one(presenter).setClientNickname(OLD_VALUE);

			one(presenter).waitForOk(); will(returnValue(true));
			one(presenter).show();

			allowing(presenter).getServerAddressHost();
				will(returnValue(NEW_VALUE));
			one(presenter).getServerAddressPort();
				will(returnValue(OLD_VALUE));
			one(presenter).getClientNickname();
				will(returnValue(NEW_VALUE));

			one(saver).set(KNOWN_KEYS[0], NEW_VALUE);
			one(saver).set(KNOWN_KEYS[2], NEW_VALUE);
		}});

		final Settings presenterSettings = new PresenterSettings(currentSettings, presenter, saver);
		presenterSettings.getString(KNOWN_KEYS[0]);
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
		final Presenter presenter = mockery.mock(Presenter.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			allowing(presenter).knownKeys();
				will(returnValue(KNOWN_KEYS));

			allowing(currentSettings).getString(KNOWN_KEYS[0]);
				will(returnValue(null));
			allowing(currentSettings).getString(KNOWN_KEYS[1]);
				will(returnValue(null));
			allowing(currentSettings).getString(KNOWN_KEYS[2]);
				will(returnValue(NOT_NULL));

			one(presenter).highlightServerAddressHost();
			one(presenter).setServerAddressHost(null);
			one(presenter).setServerAddressPort(null);
			one(presenter).setClientNickname(NOT_NULL);

			one(presenter).waitForOk(); will(returnValue(true));
			one(presenter).show();

			allowing(presenter).getServerAddressHost();
				will(returnValue(NOT_NULL));
			one(presenter).getServerAddressPort();
				will(returnValue(null));
			one(presenter).getClientNickname();
				will(returnValue(null));

			one(saver).set(KNOWN_KEYS[0], NOT_NULL);
			one(saver).set(KNOWN_KEYS[2], null);
		}});

		final Settings presenterSettings = new PresenterSettings(currentSettings, presenter, saver);
		presenterSettings.getString(KNOWN_KEYS[0]);
	}

}
