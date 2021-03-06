package ru.alepar.vuzetty.common.api;

public interface ServerRemote {
    void addTorrent(byte[] torrent, Category category);
    void addTorrent(String url, Category category);
    void pollForStats(Hash... hashes);
    void deleteTorrent(Hash hash);
    void subscribe(Category category);
}
