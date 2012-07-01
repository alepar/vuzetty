package ru.alepar.vuzetty.client.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;
import ru.alepar.vuzetty.client.play.UrlRunner;
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
    private final Map<String, DownloadStatsDisplayer> hashes = new HashMap<String, DownloadStatsDisplayer>();

    private final VuzettyClient client;

    private final JFrame frame;
    private final JPanel contentPane;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public MonitorTorrent(final VuzettyClient client) {
        this.client = client;

        client.setStatsListener(new StatsListener());

        contentPane = new JPanel();
        frame = new JFrame();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    contentPane.setLayout(new VerticalBagLayout());

                    frame.setTitle("vuzetty @ " + client.getAddress());
                    frame.setContentPane(contentPane);
                    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MonitorTorrent.class.getClassLoader().getResource(ICON_PATH)));
                    frame.setSize(445, 0);
                    frame.setVisible(true);

                    frame.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            System.exit(0);
                        }
                    });
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("something went completely bollocks", e);
        }

        new Thread(new StatPoller()).start();
    }

    @Override
    public void addTorrent(String argument) {
        log.info("adding torrent={}", argument);
        try {
            if (isLocalFile(argument)) {
                client.addTorrent(readFile(argument));
            } else {
                client.addTorrent(argument);
            }
        } catch (IOException e) {
            log.error("failed to add torrent=" + argument, e);
        }
    }

    private static boolean isLocalFile(String argument) {
        return new File(argument).isFile();
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
                    DownloadStatsPanel panel = new DownloadStatsPanel(new UrlRunner.NativeFactory(), client);
                    contentPane.add(panel.getRootPanel());
                    hashes.put(stat.hash, panel);
					displayer = panel;
                }

                displayer.updateStats(stat);
            }

            if (frame.getHeight() != frame.getPreferredSize().getHeight()) {
                frame.setSize(frame.getWidth(), (int) frame.getPreferredSize().getHeight());
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
        final InputStream is = new FileInputStream(new File(arg));
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte buffer[] = new byte[10240];
            int read;
            while ((read = is.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        } finally {
            is.close();
        }
    }

}
