package ru.alepar.vuzetty.api;

import java.io.Serializable;

public class DownloadStats implements Serializable {

    public String hash;
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

    @Override
    public String toString() {
        return "DownloadStats{" +
                "hash='" + hash + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", statusString='" + statusString + '\'' +
                ", seedsConnected=" + seedsConnected +
                ", leechersConnected=" + leechersConnected +
                ", downloadSize=" + downloadSize +
                ", percentDone=" + percentDone +
                ", downloadedBytes=" + downloadedBytes +
                ", uploadedBytes=" + uploadedBytes +
                ", downloadSpeed=" + downloadSpeed +
                ", uploadSpeed=" + uploadSpeed +
                ", seedsAvailable=" + seedsAvailable +
                ", leechersAvailable=" + leechersAvailable +
                ", availability=" + availability +
                ", shareRatio=" + shareRatio +
                ", estimatedSecsToCompletion=" + estimatedSecsToCompletion +
                '}';
    }
}
