package ru.alepar.vuzetty.common.listener;

import ru.alepar.vuzetty.common.api.TorrentInfo;

public interface TorrentListener {

    void onTorrentAdded(TorrentInfo info);

}
