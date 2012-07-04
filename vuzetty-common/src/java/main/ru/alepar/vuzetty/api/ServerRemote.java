package ru.alepar.vuzetty.api;

public interface ServerRemote {
    void addTorrent(byte[] torrent);
    void addTorrent(String url);
    void pollForStats();
    void deleteTorrent(Hash hash);
}
