package ru.alepar.vuzetty.client;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.RpcClient;
import ru.alepar.rpc.netty.NettyRpcClient;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;

public class ClientMain {

    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    private static final String MXBEAN_NAME = "ru.alepar.vuzetty.client:type=MonitorTorrent";
    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        try {
            RpcClient rpc = new NettyRpcClient(new InetSocketAddress("alepar.ru", 31337));
            TorrentApi api = rpc.getImplementation(TorrentApi.class);

            log.info("submitting torrent to vuze...");
            String hash = api.addTorrent(readFile(args[0]));
            log.info("...ok");
            MonitorTorrentMXBean monitor = findOrCreateMonitor(api);
            monitor.monitor(hash);
            if (!(monitor instanceof MonitorTorrent)) {
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("caught exception", e);
            System.exit(1);
        }
    }

    private static MonitorTorrentMXBean findOrCreateMonitor(TorrentApi api) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        MonitorTorrentMXBean monitor = findMonitor();
        if(monitor != null) {
            return monitor;
        }

        log.info("monitor not found, creating new");
        monitor = new MonitorTorrent(api);
        ManagementFactory.getPlatformMBeanServer().registerMBean(monitor, new ObjectName(MXBEAN_NAME));
        return monitor;
    }


    private static MonitorTorrentMXBean findMonitor() {
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

    private static JMXServiceURL getURLForPid(String pid) throws Exception {
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

    private static byte[] readFile(String arg) throws IOException {
        InputStream is = new FileInputStream(new File(arg));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[10240];
        int read;
        while ((read = is.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        return bos.toByteArray();
    }
}
