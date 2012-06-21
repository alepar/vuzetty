package ru.alepar.vuzetty.server.api;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.PluginManager;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ipc.IPCException;
import org.gudy.azureus2.plugins.ipc.IPCInterface;

public class VuzeMediaServerApi implements MediaServerApi {

    private final PluginManager pluginManager;

    private volatile IPCInterface mediaIpc;

    public VuzeMediaServerApi(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public String getContentURL(Download d) {
        try {
            return (String) getMediaIpc().invoke("getContentURL", new Object[]{d});
        } catch (IPCException e) {
            throw new RuntimeException("got exception while invoking azupnpav.getContentURL()", e);
        }
    }

    @Override
    public String getContentURL(DiskManagerFileInfo file) {
        try {
            return (String) getMediaIpc().invoke("getContentURL", new Object[]{file});
        } catch (IPCException e) {
            throw new RuntimeException("got exception while invoking azupnpav.getContentURL()", e);
        }
    }

    private IPCInterface getMediaIpc() {
        if (mediaIpc == null) {
            final PluginInterface mediaInterface = pluginManager.getPluginInterfaceByID("azupnpav", true);
            if(mediaInterface == null) {
                throw new RuntimeException("couldnot find azupnpav plugin - check it is installed and started up");
            }
            mediaIpc = mediaInterface.getIPC();
        }
        return mediaIpc;
    }

}
