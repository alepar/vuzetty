package ru.alepar.vuzetty.client.play.upnp;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

public class WrappedSetTransportUri extends Wrapped {

    private final String url;

    public WrappedSetTransportUri(Service avTransport, String url) {
        this.url = url;
        action = new SetAVTransportURI(avTransport, url) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                onFailure(defaultMsg);
            }

            @Override
            public void success(ActionInvocation invocation) {
                onSuccess();
            }
        };
    }

    @Override
    public String toString() {
        return String.format("SetAVTransportURI(%s)", url);
    }
}
