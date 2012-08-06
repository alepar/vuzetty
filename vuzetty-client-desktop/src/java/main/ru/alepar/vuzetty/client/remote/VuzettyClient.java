package ru.alepar.vuzetty.client.remote;

import ru.alepar.rpc.api.NettyRpcClientBuilder;
import ru.alepar.rpc.api.RpcClient;
import ru.alepar.vuzetty.common.api.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VuzettyClient implements Client {

    private final RpcClient rpc;
    private final ServerRemote api;
    private final Category category;

    private final Map<Hash, TorrentInfo> ownTorrents = new ConcurrentHashMap<Hash, TorrentInfo>();

    private StatsListener statsListener = new DummyStatsListener();
    private ru.alepar.vuzetty.common.listener.TorrentListener torrentListener = new TorrentListener();

    private volatile boolean pollOldTorrents;


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
        ownTorrents.remove(hash);
    }

    @Override
    public void pollForStats() {
        Collection<TorrentInfo> infos;
        if(pollOldTorrents) {
            infos = ownTorrents.values();
        } else {
            infos = new HashSet<TorrentInfo>();
            for (TorrentInfo info : ownTorrents.values()) {
                if(!info.old) {
                    infos.add(info);
                }
            }
        }
        if (!infos.isEmpty()) {
            final Hash[] hashes = new Hash[infos.size()];
            int i=0;
            for (TorrentInfo info : infos) {
                hashes[i++] = info.hash;
            }
            api.pollForStats(hashes);
        }
    }

    @Override
    public void setStatsListener(StatsListener listener) {
        this.statsListener = listener;
    }

    public void setTorrentListener(ru.alepar.vuzetty.common.listener.TorrentListener torrentListener) {
        this.torrentListener = torrentListener;
    }

    @Override
    public String getAddress() {
        return rpc.getRemote().getRemoteAddress();
    }

    @Override
    public TorrentInfo[] getOwnTorrents() {
        final Collection<TorrentInfo> infos = ownTorrents.values();
        return infos.toArray(new TorrentInfo[infos.size()]);
    }

    @Override
    public void setPollOldTorrents(boolean poll) {
        pollOldTorrents = poll;
    }

    public void shutdown() {
        rpc.shutdown();
    }

    public class RemoteImpl implements ClientRemote {
        @Override
        public void statsUpdated(DownloadStats[] stats) {
            statsListener.onStatsUpdate(stats);
        }

        @Override
        public void onTorrentAdded(TorrentInfo info) {
            torrentListener.onTorrentAdded(info);
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
        public void onTorrentAdded(TorrentInfo info) {
            ownTorrents.put(info.hash, info);
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
