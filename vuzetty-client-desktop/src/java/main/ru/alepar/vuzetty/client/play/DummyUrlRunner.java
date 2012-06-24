package ru.alepar.vuzetty.client.play;

public class DummyUrlRunner implements UrlRunner {
    @Override
    public void run(String url) {
        System.out.println(url);
    }
}
