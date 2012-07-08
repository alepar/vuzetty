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

	private static final String KEY = "server.address.host";
	private static final String OLD_VALUE = "value";
	private static final String NEW_VALUE = "new value";

	private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void asksCurrentSettingsIfValueExistsAndHighlightsItAndDisplaysItViaPresenter() throws Exception {
		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final Presenter presenter = mockery.mock(Presenter.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		mockery.checking(new Expectations() {{
			one(currentSettings).getString(KEY);
				will(returnValue(OLD_VALUE));

			one(presenter).highlightServerAddressHost();
			one(presenter).setServerAddressHost(OLD_VALUE);

			one(presenter).show();
			one(presenter).waitForOk();

			one(presenter).getServerAddressHost();
				will(returnValue(NEW_VALUE));

			one(saver).set(KEY, NEW_VALUE);
		}});

		final Settings presenterSettings = new PresenterSettings(currentSettings, presenter, saver);
		assertThat(presenterSettings.getString(KEY), equalTo(NEW_VALUE));
    }

	@Test
	public void populatesAllKnownKeysInPresenter() throws Exception {
		final Settings currentSettings = mockery.mock(Settings.class, "currentSettings");
		final Presenter presenter = mockery.mock(Presenter.class);
		final SettingsSaver saver = mockery.mock(SettingsSaver.class);

		final String[] knownKeys = new String[] {
				"server.address.host",
				"server.address.port",
				"client.nickname"
		};

		mockery.checking(new Expectations() {{
			ignoring(currentSettings);
			ignoring(saver);

			one(presenter).knownKeys();
				will(returnValue(knownKeys));

			one(presenter).highlightServerAddressHost();
			one(presenter).setServerAddressHost(with(any(String.class)));
			one(presenter).setServerAddressPort(with(any(String.class)));
			one(presenter).setClientNickname(with(any(String.class)));

			one(presenter).waitForOk();
			one(presenter).show();

			one(presenter).getServerAddressHost();
//			one(presenter).getServerAddressPort();
//			one(presenter).getClientNickname();
		}});

		final Settings presenterSettings = new PresenterSettings(currentSettings, presenter, saver);
		presenterSettings.getString(KEY);
	}

    @Test
    public void savesOnlyChangedValues() throws Exception {

    }

}
