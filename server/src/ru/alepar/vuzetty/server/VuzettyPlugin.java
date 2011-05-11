package ru.alepar.vuzetty.server;

import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.RpcServer;
import ru.alepar.rpc.netty.NettyRpcServer;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.server.api.VuzeTorrentApi;

import java.net.InetSocketAddress;

public class VuzettyPlugin implements Plugin {

    private static final Logger log = LoggerFactory.getLogger(VuzettyPlugin.class);

    @Override
    public void initialize(PluginInterface pluginInterface) throws PluginException {

        log.info("Starting up vuzetty");
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        TorrentManager torrentManager = pluginInterface.getTorrentManager();
        DownloadManager downloadManager = pluginInterface.getDownloadManager();

        InetSocketAddress bindAddress = new InetSocketAddress(31337);
        log.info("Binding vuzetty to " + bindAddress);
        RpcServer rpcServer = new NettyRpcServer(bindAddress);
        rpcServer.addImplementation(TorrentApi.class, new VuzeTorrentApi(torrentManager, downloadManager));

        Thread.currentThread().setContextClassLoader(oldClassLoader);
        log.info("Vuzetty is up and running");
    }

}
