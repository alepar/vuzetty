package ru.alepar.vuzetty.server.eventbus;

import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.Hash;
import ru.alepar.vuzetty.common.listener.TorrentListener;

public interface TorrentEventBus {

    void addListener(Category category, TorrentListener listener);
    void fireTorrentAdded(Hash hash, Category category);
}
