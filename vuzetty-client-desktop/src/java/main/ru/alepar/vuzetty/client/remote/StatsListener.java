package ru.alepar.vuzetty.client.remote;

import ru.alepar.vuzetty.common.api.DownloadStats;

public interface StatsListener {

    void onStatsUpdate(DownloadStats[] updatedStats);

}
