package ru.alepar.vuzetty.api;

public interface ServerApi {

    void addTorrent(byte[] torrent);
    void pollForStats();

}
