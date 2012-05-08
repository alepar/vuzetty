package ru.alepar.vuzetty.integration;

public class Support {

    public static void sleep() {
        try {
            Thread.sleep(100l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
