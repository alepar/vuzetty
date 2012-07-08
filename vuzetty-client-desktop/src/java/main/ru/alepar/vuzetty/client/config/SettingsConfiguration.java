package ru.alepar.vuzetty.client.config;

import java.net.InetSocketAddress;

public class SettingsConfiguration implements Configuration {

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
}
