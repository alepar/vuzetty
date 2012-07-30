package ru.alepar.vuzetty.client.remote;

import ru.alepar.vuzetty.common.api.Hash;

public interface Client {
    void addTorrent(byte[] torrent);
    void addTorrent(String url);
    void deleteTorrent(Hash hash);
    void pollForStats();
}
