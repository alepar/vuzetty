package ru.alepar.vuzetty.client.play;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class RuntimeCmdRunner implements CmdRunner {

    @Override
    public void run(String[] args) {
        try {
            final Process proc = Runtime.getRuntime().exec(args);
            new StreamNuller(proc.getInputStream()).start();
            new StreamNuller(proc.getErrorStream()).start();
            proc.getInputStream().close();
        } catch (Exception e) {
            throw new RuntimeException("failed to exec cmd " + Arrays.toString(args), e);
        }
    }

    private static class StreamNuller extends Thread {

        private final InputStream is;

        private StreamNuller(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while (br.readLine() != null && !isInterrupted()) { /*loop*/ }
            } catch (IOException ignored) {
                // silently exit
            }
        }
    }


}
