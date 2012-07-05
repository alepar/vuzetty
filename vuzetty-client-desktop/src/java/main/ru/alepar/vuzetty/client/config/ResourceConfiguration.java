package ru.alepar.vuzetty.client.config;

import java.net.InetSocketAddress;
import java.util.ResourceBundle;

public class ResourceConfiguration implements Configuration {

    private final ResourceBundle bundle;

    public ResourceConfiguration(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public InetSocketAddress getServerAddress() {
        return new InetSocketAddress(
                bundle.getString("server.address.host"),
                Integer.valueOf(bundle.getString("server.address.port"))
        );
    }

    @Override
    public String getNickname() {
        return null;
    }
}
