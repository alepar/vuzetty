package ru.alepar.vuzetty.client.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MonitorTorrent implements MonitorTorrentMXBean {

    private static final String ICON_PATH = "ru/alepar/vuzetty/client/gui/vuze.png";

    private final Logger log = LoggerFactory.getLogger(MonitorTorrent.class);
    private final Map<String, DownloadStatsDisplayer> hashes = new HashMap<>();

    private final VuzettyClient client;

    private final JFrame frame;
    private final JPanel contentPane;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public MonitorTorrent(VuzettyClient client) {
        this.client = client;

        client.setStatsListener(new StatsListener());

        contentPane = new JPanel();
        contentPane.setLayout(new VerticalBagLayout());

        frame = new JFrame();
        frame.setTitle("vuzetty @ " + client.getAddress());
        frame.setContentPane(contentPane);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MonitorTorrent.class.getClassLoader().getResource(ICON_PATH)));
        frame.setSize(350, 0);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        new Thread(new StatPoller()).start();
    }

    @Override
    public void addTorrent(String argument) {
        log.info("adding torrent={}", argument);
        try {
            client.addTorrent(readFile(argument));
        } catch (IOException e) {
            log.error("failed to add torrent=" + argument, e);
        }
    }

    @Override
    public boolean check() {
        return true;
    }

    private class StatsListener implements ru.alepar.vuzetty.client.remote.StatsListener {
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            for (final DownloadStats stat : updatedStats) {
                DownloadStatsDisplayer displayer = hashes.get(stat.hash);

                if(displayer == null) {
                    displayer = new DownloadStatsDisplayer();
                    contentPane.add(displayer.getRootPanel());
                    hashes.put(stat.hash, displayer);

                    frame.setSize(frame.getWidth(), (int) frame.getPreferredSize().getHeight());
                    frame.setVisible(true);
                }

                displayer.updateStats(stat);
            }
        }
    }

    private class StatPoller implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    client.pollForStats();
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static byte[] readFile(String arg) throws IOException {
        try (InputStream is = new FileInputStream(new File(arg))) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte buffer[] = new byte[10240];
            int read;
            while ((read = is.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        }
    }

}
