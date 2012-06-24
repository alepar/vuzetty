package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class DefaultThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(DefaultThreadExceptionHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("uncaught exception in " + thread, throwable);

        final StringBuilder errMsg = new StringBuilder();
        errMsg
                .append(throwable.toString())
                .append('\n');

        for (StackTraceElement element : throwable.getStackTrace()) {
            if(!element.getClassName().startsWith("ru")) {
                break;
            }
            errMsg  .append("  at ")
                    .append(element.toString())
                    .append('\n');
        }
        errMsg.append("  ...");

        JOptionPane.showMessageDialog(null, errMsg.toString(), "Exception caught", JOptionPane.ERROR_MESSAGE);
    }

}
