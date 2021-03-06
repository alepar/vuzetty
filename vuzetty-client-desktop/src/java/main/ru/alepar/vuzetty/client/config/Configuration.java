package ru.alepar.vuzetty.client.config;

import java.net.InetSocketAddress;

public interface Configuration {

    InetSocketAddress getServerAddress();

    String getNickname();

    boolean associateWithMagnetLinks();

    boolean associateWithTorrentFiles();

    String getPlayerVideo();

    boolean showOwnTorrentsByDefault();
}
