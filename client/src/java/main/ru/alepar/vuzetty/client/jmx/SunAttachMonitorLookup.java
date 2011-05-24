package ru.alepar.vuzetty.client.jmx;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.lang.management.ManagementFactory;

public class SunAttachMonitorLookup implements MonitorLookup {

    private static final Logger log = LoggerFactory.getLogger(SunAttachMonitorLookup.class);

    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    private static final String MXBEAN_NAME = "ru.alepar.vuzetty.client:type=MonitorTorrent";

    @Override
    public MonitorTorrentMXBean findOrCreateMonitor(TorrentApi api) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        MonitorTorrentMXBean monitor = findMonitor();
        if(monitor != null) {
            return monitor;
        }

        log.info("monitor not found, creating new");
        monitor = new MonitorTorrent(api);
        ManagementFactory.getPlatformMBeanServer().registerMBean(monitor, new ObjectName(MXBEAN_NAME));
        return monitor;
    }

    @Override
    public MonitorTorrentMXBean findMonitor() {
        log.info("looking for monitor");
        for (VirtualMachineDescriptor vd : VirtualMachine.list()) {
            try {
                String vmId = vd.id();
                log.debug("vmId={}, descr={}", vmId, vd.displayName());
                JMXServiceURL target = getURLForPid(vmId);
                JMXConnector connector = JMXConnectorFactory.connect(target);

                try {
                    MBeanServerConnection remote = connector.getMBeanServerConnection();
                    ObjectName name = new ObjectName(MXBEAN_NAME);
                    MonitorTorrentMXBean proxy = JMX.newMXBeanProxy(remote, name, MonitorTorrentMXBean.class);
                    proxy.check();
                    log.debug("found monitor");
                    return proxy;
                } catch(Exception e) {
                    connector.close();
                    throw e;
                }
            } catch(Exception ignored) {
                log.debug("not a monitor");
            }
        }

        return null;
    }

    private JMXServiceURL getURLForPid(String pid) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(pid);

        // get the connector address
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

        // no connector address, so we start the JMX agent
        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);
            // agent is started, get the connector address
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        }
        return new JMXServiceURL(connectorAddress);
    }
}
