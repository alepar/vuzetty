package ru.alepar.vuzetty.api;

public interface ServerRemote {
    void addTorrent(byte[] torrent, Category category);
    void addTorrent(String url, Category category);
    void pollForStats();
    void deleteTorrent(Hash hash);
}
