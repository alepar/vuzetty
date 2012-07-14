package ru.alepar.vuzetty.client.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.play.AddHostUrlRunner;
import ru.alepar.vuzetty.client.play.PlayerUrlRunner;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;
import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;
import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.Hash;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MonitorTorrent implements VuzettyRemote {

    public static final String ICON_PATH = "ru/alepar/vuzetty/client/gui/ico/vuze.png";

    private final Logger log = LoggerFactory.getLogger(MonitorTorrent.class);
    private final Map<Hash, DownloadStatsDisplayer> hashes = new HashMap<Hash, DownloadStatsDisplayer>();

    private final Configuration config;
    private final VuzettyClient client;

    private final JFrame frame;
    private final JPanel contentPane;

    public MonitorTorrent(final Configuration config, final VuzettyClient client) {
        this.config = config;
        this.client = client;

        client.setStatsListener(new StatsListener());

        contentPane = new JPanel();
        frame = new JFrame();

        try {
            final ClassLoader classLoader = MonitorTorrent.class.getClassLoader();
            final URL iconUrl = classLoader.getResource(ICON_PATH);
            final Image image = Toolkit.getDefaultToolkit().getImage(iconUrl);
            log.debug("classloader = {}", classLoader);
            frame.setIconImage(image);
            frame.setTitle(config.getNickname() + " @ " + formatAddress(client.getAddress()));

			frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

			contentPane.setLayout(new VerticalBagLayout());

            final JPanel container = new JPanel(new BorderLayout());
            container.add(contentPane, BorderLayout.CENTER);
            container.add(new StatusBar().getRootPanel(), BorderLayout.SOUTH);

			frame.setContentPane(container);
			frame.setSize(445, 0);
			frame.setVisible(true);
		} catch (Exception e) {
            throw new RuntimeException("something went completely bollocks", e);
        }

        new Thread(new StatPoller()).start();
    }

    @Override
    public void addTorrent(String argument) {
        log.info("adding torrent={}", argument);
        try {
            final Category category = new Category(config.getNickname());
            if (isLocalFile(argument)) {
                client.addTorrent(readFile(argument), category);
            } else {
                client.addTorrent(argument, category);
            }
        } catch (IOException e) {
            log.error("failed to add torrent=" + argument, e);
        }
    }

    private void toFront() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.toFront();
                frame.repaint();
            }
        });
    }

    private static String formatAddress(String address) {
        return address.replaceFirst("([^/:]+).*", "$1");
    }

    private static boolean isLocalFile(String argument) {
        return new File(argument).isFile();
    }

    private class StatsListener implements ru.alepar.vuzetty.client.remote.StatsListener {
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            for (final DownloadStats stat : updatedStats) {
                DownloadStatsDisplayer displayer = hashes.get(stat.hash);

                if(displayer == null) {
                    final DownloadStatsPanel panel = new DownloadStatsPanel(
                            client,
                            new AddHostUrlRunner(
                                    new PlayerUrlRunner(
                                            new RuntimeCmdRunner(),
                                            config.getPlayerVideo()
                                    ),
                                    config.getServerAddress().getAddress().getHostAddress()
                            )
                    );
                    panel.setDeleteListener(new DownloadStatsDisplayer.DeleteListener() {
                        @Override
                        public void onDelete() {
							hashes.remove(stat.hash);
							contentPane.remove(panel.getRootPanel());
							contentPane.revalidate();
                            frame.repaint();
							packFrameHeight();
                        }
                    });
                    contentPane.add(panel.getRootPanel());
                    hashes.put(stat.hash, panel);
					displayer = panel;

                    toFront();
                }

                displayer.updateStats(stat);
            }

			packFrameHeight();
		}
    }

	private void packFrameHeight() {
		if (frame.getHeight() != frame.getPreferredSize().getHeight()) {
			frame.setSize(frame.getWidth(), (int) frame.getPreferredSize().getHeight());
		}
	}

	private class StatPoller implements Runnable {

        @Override @SuppressWarnings("InfiniteLoopStatement")
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
