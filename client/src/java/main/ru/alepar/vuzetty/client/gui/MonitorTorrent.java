package ru.alepar.vuzetty.client.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MonitorTorrent implements MonitorTorrentMXBean {

    private static final Logger log = LoggerFactory.getLogger(MonitorTorrent.class);

    private final Map<String, DownloadStatsDisplayer> hashes = Collections.synchronizedMap(new HashMap<String, DownloadStatsDisplayer>());
    private final TorrentApi api;
    private final JFrame frame;
    private JPanel contentPane;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    public MonitorTorrent(TorrentApi api) {
        this.api = api;

        contentPane = new JPanel();
        contentPane.setLayout(new VerticalBagLayout());

        frame = new JFrame();
        frame.setTitle("vuzetty-client");
        frame.setContentPane(contentPane);
        frame.setSize(350, 0);

        final Thread thread = new Thread(new RefreshStats());
        thread.start();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void monitor(String hash) {
        log.debug("monitoring hash={}", hash);

        if(hashes.containsKey(hash)) {
            log.debug("skipping hash - already monitoring it");
            return;
        }

        DownloadStatsDisplayer displayer = new DownloadStatsDisplayer();
        contentPane.add(displayer.getRootPanel());
        hashes.put(hash, displayer);

        frame.setSize(frame.getWidth(), (int)frame.getPreferredSize().getHeight());
        frame.setVisible(true);
    }

    @Override
    public boolean check() {
        return true;
    }

    private class RefreshStats implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    try {
                        for (Map.Entry<String, DownloadStatsDisplayer> entry : hashes.entrySet()) {
                            entry.getValue().updateStats(api.getStats(entry.getKey()));
                        }
                    } catch (Exception e) {
                        log.error("failed to update stats", e);
                    }
                    frame.setSize(frame.getWidth(), (int)frame.getPreferredSize().getHeight());
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
