package ru.alepar.vuzetty.client.config;

import ru.alepar.vuzetty.client.gui.SettingsButtons;

@SuppressWarnings("UnusedDeclaration") // set/get/highlight methods below are used by Presenter via reflection
public interface SettingsView {

	String[] knownKeys();
	void setButtonListener(SettingsButtons.Listener listener);
	void show();
    void close();

	String getServerAddressHost();
	void setServerAddressHost(String value);
	void highlightServerAddressHost();

	String getServerAddressPort();
	void setServerAddressPort(String value);
	void highlightServerAddressPort();

	String getClientNickname();
	void setClientNickname(String value);
	void highlightClientNickname();

    String getAssociationMagnetlink();
    void setAssociationMagnetlink(String value);

    String getAssociationTorrentfile();
    void setAssociationTorrentfile(String value);

    String getPlayerVideo();
    void setPlayerVideo(String value);
    void highlightPlayerVideo();

}
