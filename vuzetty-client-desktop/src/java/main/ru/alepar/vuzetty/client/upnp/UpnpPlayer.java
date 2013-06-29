package ru.alepar.vuzetty.client.upnp;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.play.UrlRunner;

public class UpnpPlayer implements UrlRunner {

    private static final Logger log = LoggerFactory.getLogger(UpnpPlayer.class);

    private final UpnpService upnpService;
    private final RemoteDevice device;
    private final Service avTransport;

    public UpnpPlayer(UpnpService upnpService, RemoteDevice device, Service avTransport) {
        this.upnpService = upnpService;
        this.device = device;
        this.avTransport = avTransport;
    }

    public void play(String url) {
        log.debug("invoking setUrl({}) on {}", url, this);
        upnpService.getControlPoint().execute(new SetAVTransportURI(avTransport, url) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                log.error("failed to invoke setUri(): {}", defaultMsg);
            }

            @Override
            public void success(ActionInvocation invocation) {
                log.debug("invoking play() on {}", UpnpPlayer.this);
                upnpService.getControlPoint().execute(new Play(avTransport) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        log.error("failed to invoke play(): {}", defaultMsg);
                    }
                });
            }
        });
    }

    public String getHost() {
        return device.getDetails().getPresentationURI().getHost();
    }

    public String getName() {
        return device.getDetails().getFriendlyName();
    }

    @Override
    public String toString() {
        return String.format("UpnpPlayer{name=%s, ip=%s}", getName(), getHost());
    }

    @Override
    public void run(String url) {
        play(url);
    }
}
