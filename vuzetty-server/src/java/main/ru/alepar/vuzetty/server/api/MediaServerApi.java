package ru.alepar.vuzetty.server.api;

import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;

public interface MediaServerApi {

    String getContentURL(Download d);
    String getContentURL(DiskManagerFileInfo file);

}
