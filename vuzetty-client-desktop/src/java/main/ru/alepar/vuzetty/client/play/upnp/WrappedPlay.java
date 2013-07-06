package ru.alepar.vuzetty.client.play.upnp;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;

public class WrappedPlay extends Wrapped {

    public WrappedPlay(Service avTransport) {
        action = new Play(avTransport) {
            @Override
            public void success(ActionInvocation invocation) {
                onSuccess();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                onFailure(defaultMsg);
            }
        };
    }

    @Override
    public String toString() {
        return "Play()";
    }

}
