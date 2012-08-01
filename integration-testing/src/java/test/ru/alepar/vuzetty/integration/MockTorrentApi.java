package ru.alepar.vuzetty.integration;

import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.Hash;
import ru.alepar.vuzetty.server.vuze.TorrentApi;

import java.util.Collection;
import java.util.Collections;

public class MockTorrentApi implements TorrentApi {

    @Override
    public Hash addTorrent(byte[] torrent, Category category) {
        return new Hash(torrent);
    }

    @Override
    public Hash addTorrent(String url, Category category) {
        return new Hash(url.getBytes());
    }

    @Override
    public DownloadStats[] getStats(Collection<Hash> hashes) {
        final DownloadStats[] stats = new DownloadStats[hashes.size()];
        int i=0;
        for (Hash hash : hashes) {
            stats[i] = new DownloadStats();
            stats[i].hash = hash;
            i++;
        }
        return stats;
    }

    @Override
    public void deleteTorrent(Hash hash) {
        // do nothing
    }

    @Override
    public Collection<Hash> getHashesFor(Category category) {
        return Collections.emptySet();
    }
}
