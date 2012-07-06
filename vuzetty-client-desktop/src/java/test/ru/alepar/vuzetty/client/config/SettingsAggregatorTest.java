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
public class SettingsAggregatorTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void asksSettingsInTheOrderTheyWereProvidedAndTakesFirstNotNullReturnValue() {
        final Settings[] mocks = new Settings[] {
                mockery.mock(Settings.class, "settings_0"),
                mockery.mock(Settings.class, "settings_1"),
                mockery.mock(Settings.class, "settings_2"),
        };

        mockery.checking(new Expectations() {{
            allowing(mocks[0]).getString(KEY);
                will(returnValue(null));
            allowing(mocks[1]).getString(KEY);
                will(returnValue(VALUE));
        }});

        final SettingsAggregator aggregator = new SettingsAggregator(mocks);

        assertThat(aggregator.getString(KEY), equalTo(VALUE));
    }
}
