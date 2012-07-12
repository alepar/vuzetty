package ru.alepar.vuzetty.client.run;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

public class RuntimeCmdRunner implements CmdRunner {

    private final Logger log = LoggerFactory.getLogger(RuntimeCmdRunner.class);

    private String cmd;
    private StringBuilder stdoutBuilder;
    private StringBuilder stderrBuilder;
    private Process proc;

    @Override
    public void exec(String[] args) {
        log.debug("execing native cmd {}", Arrays.toString(args));
        cmd = args[0];
        try {
            stderrBuilder = new StringBuilder();
            stdoutBuilder = new StringBuilder();
            proc = Runtime.getRuntime().exec(args);

            new StreamReader(proc.getInputStream(), stdoutBuilder).start();
            new StreamReader(proc.getErrorStream(), stderrBuilder).start();
            proc.getOutputStream().close();
        } catch (Exception e) {
            throw new RuntimeException("failed to exec cmd " + Arrays.toString(args), e);
        }
    }

    @Override
    public int waitForExit() {
        try {
            log.debug("waiting for " + cmd);
            final int exitValue = proc.waitFor();
            log.debug("exit value " + exitValue);
            return exitValue;
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted while waiting for " + cmd);
        }
    }

    @Override @SuppressWarnings("SynchronizeOnNonFinalField")
    public String stdout() {
        synchronized (stdoutBuilder) {
            return stdoutBuilder.toString();
        }
    }

    @Override @SuppressWarnings("SynchronizeOnNonFinalField")
    public String stderr() {
        synchronized (stderrBuilder) {
            return stderrBuilder.toString();
        }
    }

    private static class StreamReader extends Thread {

        private final InputStream is;
        private final StringBuilder builder;

        private StreamReader(InputStream is, StringBuilder builder) {
            this.is = is;
            this.builder = builder;
        }

        public void run() {
            try {
                final Reader reader = new InputStreamReader(is);
                final char[] buf = new char[1];
                while (reader.read(buf) != -1 && !isInterrupted()) {
                    synchronized (builder) {
                        builder.append(buf);
                    }
                }
            } catch (IOException ignored) { }
        }
    }


}
