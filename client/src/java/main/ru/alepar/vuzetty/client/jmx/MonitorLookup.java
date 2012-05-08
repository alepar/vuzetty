package ru.alepar.vuzetty.client.jmx;

import ru.alepar.vuzetty.api.ServerApi;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public interface MonitorLookup {
    MonitorTorrentMXBean findOrCreateMonitor(ServerApi api) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException;

    MonitorTorrentMXBean findMonitor();
}
