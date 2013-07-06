package ru.alepar.vuzetty.client.play.upnp;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import ru.alepar.vuzetty.client.play.UrlPlayer;

public class UpnpUrlPlayer implements UrlPlayer {

    private final UpnpService upnpService;
    private final RemoteDevice device;
    private final Service avTransport;

    public UpnpUrlPlayer(UpnpService upnpService, RemoteDevice device, Service avTransport) {
        this.upnpService = upnpService;
        this.device = device;
        this.avTransport = avTransport;
    }

    @Override
    public void play(String url) {
        new UpnpBunch(
                new WrappedSetTransportUri(avTransport, url),
                new WrappedPlay(avTransport)
        ).executeOn(upnpService);
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

}
