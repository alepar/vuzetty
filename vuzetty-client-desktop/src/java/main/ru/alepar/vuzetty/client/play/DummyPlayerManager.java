package ru.alepar.vuzetty.client.play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPlayerManager implements PlayerManager {

    private static final Logger log = LoggerFactory.getLogger(DummyPlayerManager.class);

    @Override
    public void play(String url) {
        log.info("playing {}", url);
    }
}
