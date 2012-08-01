package ru.alepar.vuzetty.common.api;

public interface ClientRemote {
    void statsUpdated(DownloadStats[] stats);
    void onTorrentAdded(TorrentInfo info);
}
