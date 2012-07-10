package ru.alepar.vuzetty.server.vuze;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.PluginManager;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ipc.IPCException;
import org.gudy.azureus2.plugins.ipc.IPCInterface;
import ru.alepar.vuzetty.api.FileInfo;
import ru.alepar.vuzetty.server.filetype.FileTypeRecognizer;

import java.util.ArrayList;
import java.util.Collection;

public class VuzeMediaServerApi implements MediaServerApi {

    private final PluginManager pluginManager;
    private final FileTypeRecognizer fileTypeRecognizer;

    private volatile IPCInterface mediaIpc;


    public VuzeMediaServerApi(PluginManager pluginManager, FileTypeRecognizer fileTypeRecognizer) {
        this.pluginManager = pluginManager;
        this.fileTypeRecognizer = fileTypeRecognizer;
    }

    @Override
    public Collection<FileInfo> getContentUrls(Download d) {
        final Collection<FileInfo> result = new ArrayList<FileInfo>(d.getDiskManagerFileInfo().length);
        for (DiskManagerFileInfo fileInfo : d.getDiskManagerFileInfo()) {
            result.add(new FileInfo(
                    fileInfo.getFile().getName(),
                    fileInfo.getLength(),
                    getContentURL(fileInfo),
                    fileTypeRecognizer.recognize(fileInfo.getFile())));
        }
        return result;
    }

    private String getContentURL(DiskManagerFileInfo info) {
        try {
            return chopOffHostname((String) getMediaIpc().invoke("getContentURL", new Object[]{info}));
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

    private static String chopOffHostname(String url) {
        return url.replaceFirst("http://[^:/]+(.*)", "$1");
    }

}
