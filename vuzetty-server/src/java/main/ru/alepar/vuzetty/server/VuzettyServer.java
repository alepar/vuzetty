package ru.alepar.vuzetty.server;

import ru.alepar.rpc.api.ImplementationFactory;
import ru.alepar.rpc.api.NettyRpcServerBuilder;
import ru.alepar.rpc.api.Remote;
import ru.alepar.rpc.api.RpcServer;
import ru.alepar.vuzetty.api.ClientRemote;
import ru.alepar.vuzetty.api.ServerRemote;
import ru.alepar.vuzetty.server.api.Hash;
import ru.alepar.vuzetty.server.api.TorrentApi;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

public class VuzettyServer {

    private final RpcServer rpc;

    public VuzettyServer(InetSocketAddress listenAddress, TorrentApi api) {
        rpc = new NettyRpcServerBuilder(listenAddress)
                .addFactory(ServerRemote.class, new ServerRemoteHandlerFactory(api))
                .build();
    }

    public void shutdown() {
        rpc.shutdown();
    }

    public static class ServerRemoteHandlerFactory implements ImplementationFactory<ServerRemote> {

        private final TorrentApi api;

        public ServerRemoteHandlerFactory(TorrentApi api) {
            this.api = api;
        }

        @Override
        public ServerRemote create(Remote remote) {
            return new ServerRemoteHandler(api, remote);
        }
    }

    public static class ServerRemoteHandler implements ServerRemote {

        private final List<Hash> hashes = new LinkedList<>();
        private final ClientRemote clientRemote;
        private final TorrentApi api;

        public ServerRemoteHandler(TorrentApi api, Remote remote) {
            this.api = api;
            this.clientRemote = remote.getProxy(ClientRemote.class);
        }

        @Override
        public void addTorrent(byte[] torrent) {
            final Hash addedHash = api.addTorrent(torrent);
            hashes.add(addedHash);
        }

        @Override
        public void pollForStats() {
            clientRemote.statsUpdated(api.getStats(hashes));
        }
    }

}