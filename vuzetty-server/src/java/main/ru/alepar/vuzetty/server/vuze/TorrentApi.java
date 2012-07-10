package ru.alepar.vuzetty.server.vuze;

import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.Hash;

import java.util.Collection;

public interface TorrentApi {

    Hash addTorrent(byte[] torrent, Category category);
    Hash addTorrent(String url, Category category);
    DownloadStats[] getStats(Collection<Hash> hashes);
    void deleteTorrent(Hash hash);
}
