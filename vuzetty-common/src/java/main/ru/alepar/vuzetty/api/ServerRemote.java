package ru.alepar.vuzetty.api;

public interface ServerRemote {
    void addTorrent(byte[] torrent);
    void pollForStats();
}
