package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;

import java.io.*;
import java.net.InetSocketAddress;

import static ru.alepar.vuzetty.client.classload.MonitorLookupLoader.loadLookup;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);
    private static final InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("azureus.alepar.ru", 31337);

    public static void main(String[] args) throws Exception {
        try {
            log.debug("connecting to vuzetty server");
            final VuzettyClient client = new VuzettyClient(SERVER_ADDRESS);

            log.debug("submitting torrent to vuze...");
            // TODO here we need to pass to monitor not the hash but the torrent file itself
            client.addTorrent(readFile(args[0]));
            log.debug("...ok");
            MonitorTorrentMXBean monitor = loadLookup().findOrCreateMonitor(client);
            monitor.monitor("TODO");

            if (!(monitor instanceof MonitorTorrent)) { //if monitor is remote
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("caught exception", e);
            System.exit(1);
        }
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
