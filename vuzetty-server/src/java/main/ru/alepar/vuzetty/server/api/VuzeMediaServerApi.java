package ru.alepar.vuzetty.server.api;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ipc.IPCException;
import org.gudy.azureus2.plugins.ipc.IPCInterface;

public class VuzeMediaServerApi implements MediaServerApi {

    private final PluginInterface pluginInterface;

    private volatile IPCInterface mediaIpc;

    public VuzeMediaServerApi(PluginInterface pluginInterface) {
        this.pluginInterface = pluginInterface;
    }

    @Override
    public String getContentURL(Download d) {
        try {
            return (String) getMediaIpc().invoke("getContentURL", new Object[]{d});
        } catch (IPCException e) {
            throw new RuntimeException("got exception while invoking azupnpav.getContentURL()", e);
        }
    }

    private IPCInterface getMediaIpc() {
        if (mediaIpc == null) {
            final PluginInterface mediaInterface = pluginInterface.getPluginManager().getPluginInterfaceByID("azupnpav", true);
            if(mediaInterface == null) {
                throw new RuntimeException("couldnot find azupnpav plugin - check it is installed and started up");
            }
            mediaIpc = mediaInterface.getIPC();
        }
        return mediaIpc;
    }

}
