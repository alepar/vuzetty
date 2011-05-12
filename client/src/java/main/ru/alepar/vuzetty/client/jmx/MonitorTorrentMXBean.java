package ru.alepar.vuzetty.client.jmx;

public interface MonitorTorrentMXBean {
    void monitor(String hash);
    boolean check();
}
