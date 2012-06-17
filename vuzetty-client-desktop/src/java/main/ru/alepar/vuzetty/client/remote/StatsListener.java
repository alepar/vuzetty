package ru.alepar.vuzetty.client.remote;

import ru.alepar.vuzetty.api.DownloadStats;

public interface StatsListener {

    void onStatsUpdate(DownloadStats[] updatedStats);

}
