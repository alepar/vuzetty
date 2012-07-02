package ru.alepar.vuzetty.client.play;

public class AddHostUrlRunner implements UrlRunner {

    private final UrlRunner delegate;
    private final String host;

    public AddHostUrlRunner(UrlRunner delegate, String host) {
        this.delegate = delegate;
        this.host = host;
    }

    @Override
    public void run(String url) {
        delegate.run(String.format("http://%s%s", host, url));
    }

}
