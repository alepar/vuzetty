package ru.alepar.vuzetty.client.gui;

import javax.swing.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ru.alepar.vuzetty.api.DownloadStats;

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

}
