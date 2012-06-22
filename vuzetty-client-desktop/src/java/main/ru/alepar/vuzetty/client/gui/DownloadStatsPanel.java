package ru.alepar.vuzetty.client.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;

import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.api.FileInfo;

public class DownloadStatsPanel implements DownloadStatsDisplayer {

	private static final String[] TIME_SUFFIX = new String[] { "s", "m", "h", "d" };
	private static final String[] SPACE_SUFFIX = new String[] { "B", "KiB", "MiB", "GiB", "TiB" };
	private static final String NUM_FORMAT = "#,##0.0";

	private final NumberFormat format = new DecimalFormat(NUM_FORMAT);

	private JPanel torrentPanel;
	private JProgressBar progressBar;
	private JLabel downloadSpeedValue;
	private JLabel etaValue;
	private JLabel statusValue;
	private JLabel torrentSizeValue;
	private JButton playButton;

	public DownloadStatsPanel() {
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JPopupMenu popup = createMenu();
				final JComponent source = (JButton) e.getSource();
				final Point location = new Point(0, source.getHeight());
				SwingUtilities.convertPointToScreen(location, source);
				popup.setLocation(location);
				popup.setVisible(true);
			}
		});
	}

	private JPopupMenu createMenu() {
		final JPopupMenu popup = new JPopupMenu("actions");
		popup.add(createMenuItem("hi", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
				JOptionPane.showConfirmDialog(popup, "hi confirmed");
			}
		}));
		return popup;
	}

	private static JMenuItem createMenuItem(String label, ActionListener action) {
		final JMenuItem result = new JMenuItem(label);
		result.addActionListener(action);
		return result;
	}

	public JPanel getRootPanel() {
		return torrentPanel;
	}

	@Override
	public void updateStats(final DownloadStats stats) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				torrentPanel.setBorder(BorderFactory.createTitledBorder(stats.name));
				progressBar.setValue((int)stats.percentDone);
				statusValue.setText(stats.statusString);
				downloadSpeedValue.setText(formatSize(stats.downloadSpeed) + "/s");
				torrentSizeValue.setText(formatSize(stats.downloadSize));
				etaValue.setText(formatTime(stats.estimatedSecsToCompletion));
			}
		});
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

		final DownloadStatsPanel panel = new DownloadStatsPanel();
		final DownloadStats stats = new DownloadStats();
		stats.fileInfos = new HashSet<FileInfo>() {{
			add(new FileInfo("Movie A", 1024l*1024*700, "http://some url/for/movie_a.avi"));
			add(new FileInfo("Movie B", 1024l*1024*1400, "http://some url/for/movie_b.avi"));
			add(new FileInfo("Movie C", 1024l*1024*2100, "http://some url/for/movie_c.avi"));
		}};

		panel.updateStats(stats);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(panel.getRootPanel());
		frame.pack();
		frame.setVisible(true);
	}

}
