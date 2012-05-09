package ru.alepar.vuzetty.client.jmx;

import ru.alepar.vuzetty.api.ServerRemote;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public interface MonitorLookup {
    MonitorTorrentMXBean findOrCreateMonitor(ServerRemote api) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException;

    MonitorTorrentMXBean findMonitor();
}
