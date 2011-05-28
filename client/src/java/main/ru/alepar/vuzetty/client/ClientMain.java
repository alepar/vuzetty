package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.RpcClient;
import ru.alepar.rpc.netty.NettyRpcClient;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;

import java.io.*;
import java.net.InetSocketAddress;

import static ru.alepar.vuzetty.client.classload.MonitorLookupLoader.loadLookup;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        try {
            RpcClient rpc = new NettyRpcClient(new InetSocketAddress("azureus.alepar.ru", 31337));
            TorrentApi api = rpc.getImplementation(TorrentApi.class);

            log.info("submitting torrent to vuze...");
            String hash = api.addTorrent(readFile(args[0]));
            log.info("...ok");
            MonitorTorrentMXBean monitor = loadLookup().findOrCreateMonitor(api);
            monitor.monitor(hash);

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
