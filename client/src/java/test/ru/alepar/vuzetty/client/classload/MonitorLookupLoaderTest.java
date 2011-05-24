package ru.alepar.vuzetty.client.classload;

import org.junit.Test;
import ru.alepar.vuzetty.client.jmx.MonitorLookup;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonitorLookupLoaderTest {

    @Test
    public void returnsNotNullInstanceOfMonitorLookup() throws Exception {
        MonitorLookup lookup = MonitorLookupLoader.loadLookup();

        assertThat(lookup, notNullValue());
    }
}
