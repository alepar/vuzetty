package ru.alepar.vuzetty.client.remote;

import ru.alepar.vuzetty.common.api.Hash;
import ru.alepar.vuzetty.common.api.TorrentInfo;

public interface Client {

    String getAddress();
    void setStatsListener(StatsListener listener);
    void setPollOldTorrents(boolean poll);

    void addTorrent(byte[] torrent);
    void addTorrent(String url);
    void deleteTorrent(Hash hash);

    void pollForStats();
    TorrentInfo[] getOwnTorrents();

}
