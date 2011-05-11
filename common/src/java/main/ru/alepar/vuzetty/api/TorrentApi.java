package ru.alepar.vuzetty.api;

public interface TorrentApi {

    String addTorrent(byte[] torrent);
    DownloadStats getStats(String hash);

}
