package ru.alepar.vuzetty.server.api;

import ru.alepar.vuzetty.api.DownloadStats;

import java.util.Collection;

public interface TorrentApi {

    Hash addTorrent(byte[] torrent);
    DownloadStats[] getStats(Collection<Hash> hashes);

}
