package ru.alepar.vuzetty.common.api;

import java.io.Serializable;
import java.util.Collection;

public class DownloadStats implements Serializable {

    public Hash hash;
    public String name;
    public DownloadState status;
    public String statusString;
    public int seedsConnected;
    public int leechersConnected;
    public long downloadSize;
    public double percentDone;
    public long downloadedBytes;
    public long uploadedBytes;
    public long downloadSpeed;
    public long uploadSpeed;
    public int seedsAvailable;
    public int leechersAvailable;
    public float availability;
    public double shareRatio;
    public long estimatedSecsToCompletion;
    public Collection<FileInfo> fileInfos;

    @Override
    public String toString() {
        return "DownloadStats{" +
                "hash='" + hash + '\'' +
                "statusString='" + statusString + '\'' +
                '}';
    }
}
