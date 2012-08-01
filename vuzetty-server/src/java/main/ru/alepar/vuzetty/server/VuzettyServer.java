package ru.alepar.vuzetty.server;

import ru.alepar.rpc.api.ImplementationFactory;
import ru.alepar.rpc.api.NettyRpcServerBuilder;
import ru.alepar.rpc.api.Remote;
import ru.alepar.rpc.api.RpcServer;
import ru.alepar.vuzetty.common.api.*;
import ru.alepar.vuzetty.common.listener.TorrentListener;
import ru.alepar.vuzetty.server.eventbus.CategoryRespectingTorrentEventBus;
import ru.alepar.vuzetty.server.eventbus.TorrentEventBus;
import ru.alepar.vuzetty.server.vuze.TorrentApi;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class VuzettyServer {

    private final RpcServer rpc;
    private final TorrentEventBus torrentBus = new CategoryRespectingTorrentEventBus();

    public VuzettyServer(InetSocketAddress listenAddress, TorrentApi api) {
        rpc = new NettyRpcServerBuilder(listenAddress)
                .addFactory(ServerRemote.class, new ServerRemoteHandlerFactory(api, torrentBus))
                .build();
    }

    public void shutdown() {
        rpc.shutdown();
    }

    public static class ServerRemoteHandlerFactory implements ImplementationFactory<ServerRemote> {

        private final TorrentApi api;
        private final TorrentEventBus torrentBus;

        public ServerRemoteHandlerFactory(TorrentApi api, TorrentEventBus torrentBus) {
            this.api = api;
            this.torrentBus = torrentBus;
        }

        @Override
        public ServerRemote create(Remote remote) {
            return new ServerRemoteHandler(api, torrentBus, remote);
        }
    }

    public static class ServerRemoteHandler implements ServerRemote {

        private final ClientRemote clientRemote;
        private final TorrentEventBus torrentBus;
        private final TorrentApi api;

        public ServerRemoteHandler(TorrentApi api, TorrentEventBus torrentBus, Remote remote) {
            this.api = api;
            this.torrentBus = torrentBus;
            this.clientRemote = remote.getProxy(ClientRemote.class);
        }

        @Override
        public void addTorrent(byte[] torrent, Category category) {
            final Hash hash = api.addTorrent(torrent, category);
            torrentBus.fireTorrentAdded(new TorrentInfo(false, hash), category);
        }

        @Override
        public void addTorrent(String url, Category category) {
            final Hash hash = api.addTorrent(url, category);
            torrentBus.fireTorrentAdded(new TorrentInfo(false, hash), category);
        }

        @Override
        public void pollForStats(Hash... hashes) {
            clientRemote.statsUpdated(api.getStats(Arrays.asList(hashes)));
        }

        @Override
        public void deleteTorrent(Hash hash) {
            api.deleteTorrent(hash);
        }

        @Override
        public void subscribe(Category category) {
            for (Hash hash : api.getHashesFor(category)) {
                clientRemote.onTorrentAdded(new TorrentInfo(true, hash));
            }

            torrentBus.addListener(category, new TorrentListener() {
                @Override
                public void onTorrentAdded(TorrentInfo info) {
                    clientRemote.onTorrentAdded(info);
                }
            });
        }
    }

}
