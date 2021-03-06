package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.client.play.DummyPlayerManager;
import ru.alepar.vuzetty.client.play.PlayerManager;
import ru.alepar.vuzetty.client.remote.Client;
import ru.alepar.vuzetty.client.remote.StatsListener;
import ru.alepar.vuzetty.common.api.DownloadState;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.FileInfo;
import ru.alepar.vuzetty.common.api.FileType;
import ru.alepar.vuzetty.common.api.Hash;
import ru.alepar.vuzetty.common.api.TorrentInfo;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import static ru.alepar.vuzetty.common.util.FileNameUtil.*;

public class DownloadStatsPanel implements DownloadStatsDisplayer {

	private static final String[] TIME_SUFFIX = new String[] { "s", "m", "h", "d" };
	private static final String[] SPACE_SUFFIX = new String[] { "B", "KiB", "MiB", "GiB", "TiB" };
	private static final String NUM_FORMAT = "#,##0.0";

	private final NumberFormat format = new DecimalFormat(NUM_FORMAT);

    private final PlayerManager playerManager;

    private DownloadStats lastStats;

	private JPanel torrentPanel;
	private JProgressBar progressBar;
	private JLabel downloadSpeedValue;
	private JLabel etaValue;
	private JLabel statusValue;
	private JLabel torrentSizeValue;
	private JButton playButton;
    private JButton deleteButton;
    private DeleteListener listener;

    public DownloadStatsPanel(final Client client, PlayerManager playerManager) {
        this.playerManager = playerManager;
        playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JPopupMenu popup = createMenu();
				final JComponent source = (JButton) e.getSource();
				final Point location = new Point(source.getWidth() + 1, 0);
				popup.show(source, location.x, location.y);
			}
		});
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastStats.status != DownloadState.ERROR) {
                    final int choice = JOptionPane.showConfirmDialog(
                            torrentPanel,
                            "Are you sure you want to delete\n" + lastStats.name + "?",
                            "Removal confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        client.deleteTorrent(lastStats.hash);
                        fireDelete();
                    }
                } else {
                    fireDelete();
                }
            }
        });
    }

    @Override
    public void updateStats(final DownloadStats stats) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lastStats = stats;
                if (stats.name != null && !stats.name.isEmpty()) {
                    torrentPanel.setBorder(BorderFactory.createTitledBorder(stats.name));
                }
                progressBar.setValue((int)stats.percentDone);
                statusValue.setText(stats.statusString);
                downloadSpeedValue.setText(formatSize(stats.downloadSpeed) + "/s");
                torrentSizeValue.setText(formatSize(stats.downloadSize));
                etaValue.setText(formatTime(stats.estimatedSecsToCompletion));

                boolean controlsEnabled = stats.status != DownloadState.ERROR;
                playButton.setEnabled(controlsEnabled);
            }
        });
    }

    @Override
    public void setDeleteListener(DeleteListener listener) {
        this.listener = listener;
    }

    public JPanel getRootPanel() {
        return torrentPanel;
    }

    private void fireDelete() {
        if(listener != null) {
            listener.onDelete();
        }
    }

	private JPopupMenu createMenu() {
		final JPopupMenu popup = new JPopupMenu("actions");
        final SortedSet<FileInfo> sortedInfos = new TreeSet<FileInfo>(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo left, FileInfo right) {
                if(left == null || right == null) {
                    if(left == null && right != null) return -1;
                    if(left != null) return 1;
                    return 0;
                }
                return left.name.compareTo(right.name);
            }
        });
        sortedInfos.addAll(lastStats.fileInfos);
        for (final FileInfo info : sortedInfos) {
            if(info.type == FileType.VIDEO) {
                final String label = String.format("video %s %s - %s", extractFileExtension(info.name), formatSize(info.length), info.name);
                createMenuItem(popup, label, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playerManager.play(info.url);
                    }
                });
            }
        }

		return popup;
	}

    private static void createMenuItem(JPopupMenu menu, String label, ActionListener action) {
		final JMenuItem result = new JMenuItem(label);
		result.addActionListener(action);
		menu.add(result);
	}

	private String format(Long num) {
		Double s = (double) num;
		int c = 0;
		while((c < 4) && (s / 1024 >= 1)) {
			c++;
			s /= 1024;
		}
		return format.format(s) + " " + SPACE_SUFFIX[c];
	}

	private String formatSize(long bytes) {
		return format(bytes);
	}

	private static String formatTime(long secs) {
		if (secs < 0 || secs >= 3600*24*100) {
			return "∞";
		}
		long[] times = new long[4];
		times[0] = secs % 60;
		times[1] = (secs/60) % 60;
		times[2] = (secs/3600) % 24;
		times[3] = (secs/3600/24);

		StringBuilder sb = new StringBuilder();
		for (int i = times.length-1; i >= 0 ; i--) {
			if (times[i] > 0) {
				sb.append(times[i]).append(TIME_SUFFIX[i]).append(' ');
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final JFrame frame = new JFrame();

        final JPanel container = new JPanel(new BorderLayout());
        final JPanel panels = new JPanel();
        panels.setLayout(new BoxLayout(panels, BoxLayout.Y_AXIS));

		DownloadStats stats;

        final DummyPlayerManager playerManager = new DummyPlayerManager();
        final DownloadStatsPanel statsPanelOne = new DownloadStatsPanel(new DummyClient(), playerManager);
        stats = new DownloadStats();
        stats.hash = new Hash("cafebabe");
        stats.name = "Movies";
		stats.fileInfos = new HashSet<FileInfo>() {{
			add(new FileInfo("3 Movie A.avi", 1024l*1024*700, ":45707/Content/944D2E1C1443008DDFA34A89AEC282393AC8D883-0.mkv", FileType.VIDEO));
			add(new FileInfo("2 Movie B.mvk", 1024l*1024*1400, "http://somehost/for/movie_b.mkv", FileType.VIDEO));
			add(new FileInfo("1 Movie C.vob", 1024l*1024*2100, "http://somehost/for/movie_c.vob", FileType.VIDEO));
		}};
        statsPanelOne.setDeleteListener(new DeleteListener() {
            @Override
            public void onDelete() {
                panels.remove(statsPanelOne.getRootPanel());
                panels.revalidate();
                frame.repaint();
            }
        });
        statsPanelOne.updateStats(stats);
        panels.add(statsPanelOne.getRootPanel());

        final DownloadStatsPanel statsPanelTwo = new DownloadStatsPanel(new DummyClient(), playerManager);
		stats = new DownloadStats();
        stats.hash = new Hash("deadbeef");
        stats.name = "BigBangTheory";
		stats.fileInfos = new HashSet<FileInfo>() {{
			add(new FileInfo("BigBang_s05e01.avi", 1024l*1024*500, "http://somehost/for/BigBang_s05e01.avi", FileType.VIDEO));
			add(new FileInfo("BigBang_s05e01.srt", 1024l*102, "http://somehost/for/BigBang_s05e01.srt", FileType.UNKNOWN));
		}};
        statsPanelTwo.setDeleteListener(new DeleteListener() {
            @Override
            public void onDelete() {
                panels.remove(statsPanelTwo.getRootPanel());
                panels.revalidate();
                frame.repaint();
            }
        });
        statsPanelTwo.updateStats(stats);
        panels.add(statsPanelTwo.getRootPanel());

        final DownloadStatsPanel statsPanelThree = new DownloadStatsPanel(new DummyClient(), playerManager);
		stats = new DownloadStats();
        stats.hash = new Hash("d34df00d");
        stats.name = "";
        stats.statusString = "Not Found";
        stats.status = DownloadState.ERROR;
        statsPanelThree.setDeleteListener(new DeleteListener() {
            @Override
            public void onDelete() {
                panels.remove(statsPanelThree.getRootPanel());
                panels.revalidate();
                frame.repaint();
            }
        });
        statsPanelThree.updateStats(stats);
        panels.add(statsPanelThree.getRootPanel());

        container.add(panels, BorderLayout.CENTER);
        container.add(new StatusBar(false, null).getRootPanel(), BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(container);
		frame.pack();
		frame.setVisible(true);
	}

    private static class DummyClient implements Client {
        @Override
        public void addTorrent(byte[] torrent) {
            System.out.println("ServerRemote#addTorrent(" + Arrays.toString(torrent)+")");
        }

        @Override
        public void addTorrent(String url) {
            System.out.println("ServerRemote#addTorrent(" + url+")");
        }

        @Override
        public void deleteTorrent(Hash hash) {
            System.out.println("ServerRemote#deleteTorrent(" + hash +")");
        }

        @Override
        public void pollForStats() {
            System.out.println("ServerRemote#pollForStats()");
        }

        @Override
        public void setPollOldTorrents(boolean poll) {
            throw new RuntimeException("parfenal, implement me!");
        }

        @Override
        public String getAddress() {
            throw new RuntimeException("parfenal, implement me!");
        }

        @Override
        public TorrentInfo[] getOwnTorrents() {
            throw new RuntimeException("parfenal, implement me!");
        }

        @Override
        public void setStatsListener(StatsListener listener) {
            throw new RuntimeException("parfenal, implement me!");
        }
    }
}
