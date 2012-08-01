package ru.alepar.vuzetty.client.config;

import java.net.InetSocketAddress;

public class SettingsConfiguration implements Configuration {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private final Settings settings;

    public SettingsConfiguration(Settings settings) {
		this.settings = settings;
	}

	@Override
	public InetSocketAddress getServerAddress() {
		return new InetSocketAddress(
				settings.getString("server.address.host"),
				Integer.valueOf(settings.getString("server.address.port"))
		);
	}

	@Override
	public String getNickname() {
		return settings.getString("client.nickname");
	}

    @Override
    public boolean associateWithMagnetLinks() {
        return TRUE.equals(settings.getString("association.magnetlink"));
    }

    @Override
    public boolean associateWithTorrentFiles() {
        return TRUE.equals(settings.getString("association.torrentfile"));
    }

    @Override
    public String getPlayerVideo() {
        return settings.getString("player.video");
    }

    @Override
    public boolean showOwnTorrentsByDefault() {
        return TRUE.equals(settings.getString("owntorrents.show"));
    }
}
