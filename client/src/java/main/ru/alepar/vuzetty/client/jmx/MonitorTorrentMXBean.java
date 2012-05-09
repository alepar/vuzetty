package ru.alepar.vuzetty.client.jmx;

public interface MonitorTorrentMXBean {
    void addTorrent(String argument);
    boolean check();
}
