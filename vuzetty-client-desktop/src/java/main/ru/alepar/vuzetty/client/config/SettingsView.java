package ru.alepar.vuzetty.client.config;

import ru.alepar.vuzetty.client.gui.SettingsButtons;

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

    interface Factory {
        SettingsView create();
    }

}
