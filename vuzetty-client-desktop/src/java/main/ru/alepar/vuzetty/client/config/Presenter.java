package ru.alepar.vuzetty.client.config;

public interface Presenter {

	String[] knownKeys();
	boolean waitForOk();
	void show();

	String getServerAddressHost();
	void setServerAddressHost(String value);
	void highlightServerAddressHost();

	String getServerAddressPort();
	void setServerAddressPort(String value);
	void highlightServerAddressPort();

	String getClientNickname();
	void setClientNickname(String value);
	void highlightClientNickname();
}
