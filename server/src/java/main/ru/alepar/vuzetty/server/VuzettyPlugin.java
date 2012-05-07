package ru.alepar.vuzetty.server;

import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.api.NettyRpcServerBuilder;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.server.api.VuzeTorrentApi;

import java.net.InetSocketAddress;

public class VuzettyPlugin implements Plugin {

    private static final Logger log = LoggerFactory.getLogger(VuzettyPlugin.class);

    @Override
    public void initialize(PluginInterface pluginInterface) throws PluginException {

        log.info("starting up vuzetty server ");
        final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        final TorrentManager torrentManager = pluginInterface.getTorrentManager();
        final DownloadManager downloadManager = pluginInterface.getDownloadManager();

        final InetSocketAddress bindAddress = new InetSocketAddress(31337);
        log.info("binding vuzetty to " + bindAddress);

        new NettyRpcServerBuilder(bindAddress)
                .addObject(TorrentApi.class, new VuzeTorrentApi(torrentManager, downloadManager))
                .build();

        Thread.currentThread().setContextClassLoader(oldClassLoader);
        log.info("vuzetty server is up and running");
    }

}
