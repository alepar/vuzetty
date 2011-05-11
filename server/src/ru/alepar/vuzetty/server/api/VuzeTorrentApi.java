package ru.alepar.vuzetty.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.api.TorrentApi;

public class VuzeTorrentApi implements TorrentApi {

    private static final Logger log = LoggerFactory.getLogger(VuzeTorrentApi.class);

    public boolean addTorrent(byte[] torrent) {
        log.info("got torrent file, size=" + torrent.length);
        return true;
    }

}
