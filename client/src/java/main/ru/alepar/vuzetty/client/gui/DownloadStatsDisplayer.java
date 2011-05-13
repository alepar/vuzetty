package ru.alepar.vuzetty.client.gui;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import ru.alepar.vuzetty.api.DownloadStats;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DownloadStatsDisplayer {

    private static final String[] TIME_SUFFIX = new String[] { "sec", "min", "h", "d" };
    private static final String[] SPACE_SUFFIX = new String[] { "B", "KiB", "MiB", "GiB", "TiB" };
    private static final String NUM_FORMAT = "#,##0.0";

    private final NumberFormat format = new DecimalFormat(NUM_FORMAT);

    private JPanel torrentPanel;

    private JProgressBar progressBar;
    private JPanel statsPanel;

    private JLabel statusCaption;
    private JLabel downloadSpeedCaption;
    private JLabel torrentSizeCaption;
    private JLabel etaCaption;

    private JLabel statusValue;
    private JLabel downloadSpeedValue;
    private JLabel torrentSizeValue;
    private JLabel etaValue;

    public DownloadStatsDisplayer() {
        torrentPanel = new JPanel();
        torrentPanel.setLayout(new FormLayout("fill:d:grow", "center:p:noGrow,top:4dlu:noGrow,center:p:noGrow"));
        torrentPanel.setBorder(BorderFactory.createTitledBorder(" - "));

        progressBar = new JProgressBar();
        progressBar.setValue(0);
        CellConstraints cc = new CellConstraints();
        torrentPanel.add(progressBar, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

        statsPanel = new JPanel();
        statsPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:14px:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:p:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        torrentPanel.add(statsPanel, cc.xy(1, 3));

        statusCaption = new JLabel();
        statusCaption.setText("Status  ");
        statsPanel.add(statusCaption, cc.xy(1, 1));
        statusValue = new JLabel();
        statusValue.setText(" - ");
        statsPanel.add(statusValue, cc.xy(3, 1));

        torrentSizeCaption = new JLabel();
        torrentSizeCaption.setText("Size  ");
        statsPanel.add(torrentSizeCaption, cc.xy(1, 3));
        torrentSizeValue = new JLabel();
        torrentSizeValue.setText(" - ");
        statsPanel.add(torrentSizeValue, cc.xy(3, 3));

        final Spacer spacer1 = new Spacer();
        statsPanel.add(spacer1, cc.xy(5, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

        downloadSpeedCaption = new JLabel();
        downloadSpeedCaption.setText("Down speed  ");
        statsPanel.add(downloadSpeedCaption, cc.xy(7, 1));
        downloadSpeedValue = new JLabel();
        downloadSpeedValue.setText(" - ");
        statsPanel.add(downloadSpeedValue, cc.xy(9, 1));

        etaCaption = new JLabel();
        etaCaption.setText("ETA  ");
        statsPanel.add(etaCaption, cc.xy(7, 3));
        etaValue = new JLabel();
        etaValue.setText(" - ");
        statsPanel.add(etaValue, cc.xy(9, 3));
    }

    public JPanel getRootPanel() {
        return torrentPanel;
    }

    public void updateStats(DownloadStats stats) {
        torrentPanel.setBorder(BorderFactory.createTitledBorder(stats.name));
        progressBar.setValue((int)stats.percentDone);
        statusValue.setText(stats.statusString);
        downloadSpeedValue.setText(formatSize(stats.downloadSpeed) + "/s");
        torrentSizeValue.setText(formatSize(stats.downloadSize));
        etaValue.setText(formatTime(stats.estimatedSecsToCompletion));
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
        if (secs < 0) {
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
