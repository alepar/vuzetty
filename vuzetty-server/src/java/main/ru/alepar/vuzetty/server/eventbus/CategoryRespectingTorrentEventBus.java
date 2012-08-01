package ru.alepar.vuzetty.server.eventbus;

import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.TorrentInfo;
import ru.alepar.vuzetty.common.listener.TorrentListener;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CategoryRespectingTorrentEventBus implements TorrentEventBus {

    private final List<Map.Entry<Category, TorrentListener>> listeners = new LinkedList<Map.Entry<Category, TorrentListener>>();

    @Override
    public void fireTorrentAdded(TorrentInfo info, Category category) {
        for (Map.Entry<Category, TorrentListener> entry : listeners) {
            if(entry.getKey().equals(category)) {
                entry.getValue().onTorrentAdded(info);
            }
        }
    }

    @Override
    public void addListener(Category category, TorrentListener listener) {
        listeners.add(new AbstractMap.SimpleEntry<Category, TorrentListener>(category, listener));
    }

}
