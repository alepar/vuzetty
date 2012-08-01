package ru.alepar.vuzetty.common.api;

import java.io.Serializable;

public class TorrentInfo implements Serializable {

    public final boolean old;
    public final Hash hash;

    public TorrentInfo(boolean old, Hash hash) {
        this.old = old;
        this.hash = hash;
    }

}
