package ru.alepar.vuzetty.client.play.upnp;

import org.fourthline.cling.controlpoint.ActionCallback;

public class Wrapped {

    protected volatile ActionCallback action;

    private volatile UpnpBunch bunch;

    public void setBunch(UpnpBunch bunch) {
        this.bunch = bunch;
    }

    protected void onSuccess() {
        bunch.onSuccess(this);
    }

    protected void onFailure(String message) {
        bunch.onFailure(this, message);
    }
}
