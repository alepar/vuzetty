package ru.alepar.vuzetty.client.config;

public interface Presenter {

	void waitForOk();
	void show();

	String getServerAddressHost();
	void setServerAddressHost(String value);
	void highlightServerAddressHost();
}
