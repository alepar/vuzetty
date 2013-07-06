package ru.alepar.vuzetty.client.play.upnp;

import org.fourthline.cling.UpnpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpnpBunch {

    private static final Logger log = LoggerFactory.getLogger(UpnpBunch.class);
    private static final int MAX_TRIES = 3;

    private final Wrapped[] actions;

    private volatile UpnpService upnpService;
    private volatile int retryCount;

    public UpnpBunch(Wrapped... actions) {
        this.actions = actions;

        for (Wrapped action : actions) {
            action.setBunch(this);
        }
    }

    public void onSuccess(Wrapped wrapped) {
        log.info("upnp action {} successful", wrapped);
        retryCount = 0;
        executeAction(findActionIndex(wrapped)+1);
    }

    public void onFailure(Wrapped wrapped, String message) {
        log.warn("upnp action {} failed, reason={}", wrapped, message);
        if (++retryCount < MAX_TRIES) {
            log.info("retrying {}", wrapped);
            executeAction(findActionIndex(wrapped));
        } else {
            log.error("MAX_TRIES reached, aborting");
        }
    }

    public void executeOn(UpnpService upnpService) {
        this.upnpService = upnpService;
        executeAction(0);
    }

    private int findActionIndex(Wrapped wrapped) {
        for (int i = 0; i < actions.length; i++) {
            if (wrapped == actions[i]) {
                return i;
            }
        }
        return actions.length;
    }

    private void executeAction(int index) {
        if (index >= 0 && index < actions.length) {
            upnpService.getControlPoint().execute(actions[index].action);
        }
    }

}
