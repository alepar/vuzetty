package ru.alepar.vuzetty.client.gui;

import ru.alepar.vuzetty.common.api.DownloadStats;

public interface DownloadStatsDisplayer {

	void updateStats(DownloadStats stats);
    void setDeleteListener(DeleteListener listener);

    interface DeleteListener {
        void onDelete();
    }

}
