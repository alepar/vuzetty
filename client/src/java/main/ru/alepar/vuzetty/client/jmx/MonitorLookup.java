package ru.alepar.vuzetty.client.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public interface MonitorLookup {

    void registerMonitor(MonitorTorrentMXBean monitor) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException;
    MonitorTorrentMXBean findMonitor();

}
