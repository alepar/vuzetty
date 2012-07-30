package ru.alepar.vuzetty.client.remote;

import ru.alepar.rpc.api.NettyRpcClientBuilder;
import ru.alepar.rpc.api.RpcClient;
import ru.alepar.vuzetty.common.api.*;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class VuzettyClient implements Client {

    private final RpcClient rpc;
    private final ServerRemote api;
    private final Category category;

    private final Set<Hash> subscribedHashes = new HashSet<Hash>();

    private StatsListener statsListener = new DummyStatsListener();
    private ru.alepar.vuzetty.common.listener.TorrentListener torrentListener = new TorrentListener();

    public VuzettyClient(InetSocketAddress bindAddress, String nickname) {
        category = new Category(nickname);
        rpc = new NettyRpcClientBuilder(bindAddress)
                    .addObject(ClientRemote.class, new RemoteImpl())
                    .build();
        api = rpc.getRemote().getProxy(ServerRemote.class);

        api.subscribe(category);

        new Thread(new StatPoller()).start();
    }

    @Override
    public void addTorrent(byte[] torrent) {
        api.addTorrent(torrent, category);
    }

    @Override
    public void addTorrent(String url) {
        api.addTorrent(url, category);
    }

    @Override
    public void deleteTorrent(Hash hash) {
        api.deleteTorrent(hash);
    }

    @Override
    public void pollForStats() {
        if (!subscribedHashes.isEmpty()) {
            api.pollForStats(subscribedHashes.toArray(new Hash[subscribedHashes.size()]));
        }
    }

    public void setStatsListener(StatsListener listener) {
        this.statsListener = listener;
    }

    public void setTorrentListener(ru.alepar.vuzetty.common.listener.TorrentListener torrentListener) {
        this.torrentListener = torrentListener;
    }

    public void shutdown() {
        rpc.shutdown();
    }

    public String getAddress() {
        return rpc.getRemote().getRemoteAddress();
    }

    public class RemoteImpl implements ClientRemote {
        @Override
        public void statsUpdated(DownloadStats[] stats) {
            statsListener.onStatsUpdate(stats);
        }

        @Override
        public void onTorrentAdded(Hash hash) {
            torrentListener.onTorrentAdded(hash);
        }
    }

    private static class DummyStatsListener implements StatsListener {
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            // do nothing
        }
    }

    private class TorrentListener implements ru.alepar.vuzetty.common.listener.TorrentListener {
        @Override
        public void onTorrentAdded(Hash hash) {
            subscribedHashes.add(hash);
        }
    }

    private class StatPoller implements Runnable {

        @Override @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            try {
                while (true) {
                    pollForStats();
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

}
