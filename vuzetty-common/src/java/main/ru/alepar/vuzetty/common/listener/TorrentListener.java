package ru.alepar.vuzetty.common.listener;

import ru.alepar.vuzetty.common.api.Hash;

public interface TorrentListener {

    void onTorrentAdded(Hash hash);

}
