package ru.alepar.vuzetty.integration;

import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.server.api.Hash;
import ru.alepar.vuzetty.server.api.TorrentApi;

import java.util.Collection;

public class MockTorrentApi implements TorrentApi {

    @Override
    public Hash addTorrent(byte[] torrent) {
        return new Hash(torrent);
    }

    @Override
    public Hash addTorrent(String url) {
        return new Hash(url.getBytes());
    }

    @Override
    public DownloadStats[] getStats(Collection<Hash> hashes) {
        final DownloadStats[] stats = new DownloadStats[hashes.size()];
        int i=0;
        for (Hash hash : hashes) {
            stats[i] = new DownloadStats();
            stats[i].hash = hash.toString();
            i++;
        }
        return stats;
    }
}
