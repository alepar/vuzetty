package ru.alepar.vuzetty.server.api;

import org.gudy.azureus2.plugins.download.Download;
import ru.alepar.vuzetty.api.FileInfo;

import java.util.Collection;

public interface MediaServerApi {

    Collection<FileInfo> getContentUrls(Download d);

}
