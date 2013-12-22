package ru.alepar.vuzetty.client.play.upnp;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class UpnpControl {

    public interface PlayerListener {
        void onPlayers(Collection<UpnpUrlPlayer> players);
    }

    private static final Logger log = LoggerFactory.getLogger(UpnpControl.class);

    static final UnsignedIntegerFourBytes DEFAULT_INSTANCE_ID = new UnsignedIntegerFourBytes("0");

    private final UpnpService upnpService = new UpnpServiceImpl(new ApacheUpnpServiceConfiguration());
    private final ServiceId serviceId = new UDAServiceId("AVTransport");

    private final Map<Device, UpnpUrlPlayer> players = Maps.newConcurrentMap();
    private final Set<PlayerListener> listeners = Sets.newCopyOnWriteArraySet();

    public UpnpControl() {
        try {
             // Add a listener for device registration events
             upnpService.getRegistry().addListener(new RegistryListener());
             // Broadcast a search message for all devices
             upnpService.getControlPoint().search(new STAllHeader());
         } catch (Exception ex) {
             throw new RuntimeException("failed to start", ex);
         }
    }

    public Collection<UpnpUrlPlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players.values());
    }

    public void subscribe(PlayerListener listener) {
        listeners.add(listener);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void unsubscribe(PlayerListener listener) {
        listeners.remove(listener);
    }

    private void onPlayers() {
        final Collection<UpnpUrlPlayer> players = getPlayers();
        for (PlayerListener listener : listeners) {
            try {
                listener.onPlayers(players);
            } catch (Exception e) {
                log.error("listener {} thrown exception {}", listener, e);
            }
        }
    }

    private class RegistryListener extends DefaultRegistryListener {
        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            final Service avTransport;
            if ((avTransport = device.findService(serviceId)) != null) {
                final UpnpUrlPlayer player = new UpnpUrlPlayer(upnpService, device, avTransport);
                players.put(device, player);
                log.debug("new player found: {}", player);
                onPlayers();
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            final UpnpUrlPlayer player = players.remove(device);
            if (player != null) {
                log.debug("player removed: {}", player);
                onPlayers();
            }
        }
    }

}
