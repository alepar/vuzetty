package ru.alepar.vuzetty.client.remote;

import ru.alepar.rpc.api.NettyRpcClientBuilder;
import ru.alepar.rpc.api.RpcClient;
import ru.alepar.vuzetty.api.ClientRemote;
import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.api.ServerRemote;

import java.net.InetSocketAddress;

public class VuzettyClient implements ServerRemote {

    private final RpcClient rpc;
    private final ServerRemote api;

    private StatsListener listener = new DummyListener();

    public VuzettyClient(InetSocketAddress bindAddress) {
        rpc =
                new NettyRpcClientBuilder(bindAddress)
                        .addObject(ClientRemote.class, new RemoteImpl())
                        .build();
        api = rpc.getRemote().getProxy(ServerRemote.class);
    }

    @Override
    public void addTorrent(byte[] torrent) {
        api.addTorrent(torrent);
    }

    @Override
    public void pollForStats() {
        api.pollForStats();
    }

    public void setStatsListener(StatsListener listener) {
        this.listener = listener;
    }

    public void shutdown() {
        rpc.shutdown();
    }

    public class RemoteImpl implements ClientRemote {
        @Override
        public void statsUpdated(DownloadStats[] stats) {
            listener.onStatsUpdate(stats);
        }
    }

    private static class DummyListener implements StatsListener {
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            //ignore
        }
    }
}
