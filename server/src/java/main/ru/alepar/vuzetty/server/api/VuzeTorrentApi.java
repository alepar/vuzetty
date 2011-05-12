package ru.alepar.vuzetty.server.api;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import ru.alepar.vuzetty.api.DownloadState;
import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.api.TorrentApi;

import java.math.BigInteger;

public class VuzeTorrentApi implements TorrentApi {

    private final TorrentManager torrentManager;
    private final DownloadManager downloadManager;

    public VuzeTorrentApi(TorrentManager torrentManager, DownloadManager downloadManager) {
        this.torrentManager = torrentManager;
        this.downloadManager = downloadManager;
    }

    public String addTorrent(byte[] torrent) {
        try {
            return hashToString(downloadManager.addDownload(torrentManager.createFromBEncodedData(torrent)).getTorrent().getHash());
        } catch (Exception e) {
            throw new RuntimeException("failed to add torrent", e);
        }
    }

    private static String hashToString(byte[] hash) {
        return new BigInteger(hash).toString(16);
    }

    private static byte[] stringToHash(String hash) {
        return new BigInteger(hash, 16).toByteArray();
    }

    @Override
    public DownloadStats getStats(String hash) {
        try {
            Download torrent = findTorrent(hash);
            DownloadStats result = new DownloadStats();
            result.hash = hash;
            result.name = torrent.getName();

            result.status = translate(torrent.getState());
            result.statusString = torrent.getStats().getStatus();

            if (torrent.getPeerManager().getStats() != null) {
                result.seedsConnected = torrent.getPeerManager().getStats().getConnectedSeeds();
                result.leechersConnected = torrent.getPeerManager().getStats().getConnectedLeechers();
            } else {
                result.seedsConnected = 0;
                result.leechersConnected = 0;
            }

            result.downloadSize = torrent.getTorrent().getSize();
            result.percentDone = torrent.getStats().getCompleted() / 10.0;

            result.downloadedBytes = torrent.getStats().getDownloaded();
            result.uploadedBytes = torrent.getStats().getUploaded();

            result.downloadSpeed = torrent.getStats().getDownloadAverage();
            result.uploadSpeed = torrent.getStats().getUploadAverage();

            result.seedsAvailable = torrent.getLastScrapeResult().getSeedCount();
            result.leechersAvailable = torrent.getLastScrapeResult().getNonSeedCount();

            result.availability = torrent.getStats().getAvailability();
            result.shareRatio = torrent.getStats().getShareRatio() / 1000.0;
            result.estimatedSecsToCompletion = torrent.getStats().getETASecs();

            return result;
        } catch (Exception e) {
            throw new RuntimeException("failed to get stats for hash=" + hash, e);
        }
    }

    private static DownloadState translate(int state) {
        switch (state) {
            case Download.ST_WAITING:
                return DownloadState.WAITING;
            case Download.ST_PREPARING:
                return DownloadState.PREPARING;
            case Download.ST_READY:
                return DownloadState.READY;
            case Download.ST_DOWNLOADING:
                return DownloadState.DOWNLOADING;
            case Download.ST_SEEDING:
                return DownloadState.SEEDING;
            case Download.ST_STOPPING:
                return DownloadState.STOPPING;
            case Download.ST_STOPPED:
                return DownloadState.STOPPED;
            case Download.ST_ERROR:
                return DownloadState.ERROR;
            case Download.ST_QUEUED:
                return DownloadState.QUEUED;
            default:
                return null;
        }
    }

    private Download findTorrent(String hash) throws DownloadException {
        return downloadManager.getDownload(stringToHash(hash));
    }
}
