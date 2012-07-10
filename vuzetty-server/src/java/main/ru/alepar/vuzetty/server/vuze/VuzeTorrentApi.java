package ru.alepar.vuzetty.server.vuze;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.utils.Utilities;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.DownloadState;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.Hash;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VuzeTorrentApi implements TorrentApi {

    private final TorrentManager torrentManager;
    private final DownloadManager downloadManager;
    private final Utilities utilities;
    private final MediaServerApi mediaServer;
    private final TorrentAttribute categoryAttr;

    public VuzeTorrentApi(TorrentManager torrentManager, DownloadManager downloadManager, Utilities utilities, MediaServerApi mediaServer) {
        this.torrentManager = torrentManager;
        this.downloadManager = downloadManager;
        this.utilities = utilities;
        this.mediaServer = mediaServer;
        this.categoryAttr = torrentManager.getAttribute(TorrentAttribute.TA_CATEGORY);
    }

    @Override
    public Hash addTorrent(byte[] bytes, Category category) {
        try {
            final Torrent torrent = torrentManager.createFromBEncodedData(bytes);
            return addTorrent(torrent, category);
        } catch (Exception e) {
            throw new RuntimeException("failed to add torrent", e);
        }
    }

    @Override
    public Hash addTorrent(String urlStr, Category category) {
        try {
            final URL url = new URL(urlStr);
            final ResourceDownloader rd = utilities.getResourceDownloaderFactory().create(url);
            final InputStream is = rd.download();
            final Torrent torrent = torrentManager.createFromBEncodedInputStream(is);
            return addTorrent(torrent, category);
        } catch (Exception e) {
            throw new RuntimeException("failed to add torrent", e);
        }
    }

    private Hash addTorrent(Torrent torrent, Category category) throws DownloadException {
        final Download download = downloadManager.addDownload(torrent);
        if(!isCategoryDefined(category)) {
            categoryAttr.addDefinedValue(category.name);
        }
        download.setAttribute(categoryAttr, category.name);
        return new Hash(torrent.getHash());
    }

    private boolean isCategoryDefined(Category category) {
        for (String name : categoryAttr.getDefinedValues()) {
            if(name.equals(category.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DownloadStats[] getStats(Collection<Hash> hashes) {
        final List<DownloadStats> stats = new ArrayList<DownloadStats>(hashes.size());
        for (Hash hash : hashes) {
            try {
                stats.add(extractStats(downloadManager.getDownload(hash.bytes())));
            } catch (Exception e) {
                final DownloadStats stat = new DownloadStats();
                stat.hash = hash;
                stat.errorMessage = e.toString();
                stats.add(stat);
            }
        }
        return stats.toArray(new DownloadStats[stats.size()]);
    }

    @Override
    public void deleteTorrent(Hash hash) {
        try {
            final Download download = downloadManager.getDownload(hash.bytes());
            download.stopDownload();
            download.remove(true, true);
        } catch (Exception e) {
            throw new RuntimeException("failed to remove download " + hash, e);
        }
    }

    private DownloadStats extractStats(Download download) {
        final DownloadStats stat = new DownloadStats();

        stat.hash = new Hash(download.getTorrent().getHash());
        stat.name = download.getName();

        stat.status = translate(download.getState());
        stat.statusString = download.getStats().getStatus();

        if (download.getPeerManager() != null && download.getPeerManager().getStats() != null) {
            stat.seedsConnected = download.getPeerManager().getStats().getConnectedSeeds();
            stat.leechersConnected = download.getPeerManager().getStats().getConnectedLeechers();
        } else {
            stat.seedsConnected = 0;
            stat.leechersConnected = 0;
        }

        stat.downloadSize = download.getTorrent().getSize();
        stat.percentDone = download.getStats().getCompleted() / 10.0;

        stat.downloadedBytes = download.getStats().getDownloaded();
        stat.uploadedBytes = download.getStats().getUploaded();

        stat.downloadSpeed = download.getStats().getDownloadAverage();
        stat.uploadSpeed = download.getStats().getUploadAverage();

        if (download.getLastScrapeResult() != null) {
            stat.seedsAvailable = download.getLastScrapeResult().getSeedCount();
            stat.leechersAvailable = download.getLastScrapeResult().getNonSeedCount();
        } else {
            stat.seedsAvailable = -1;
            stat.leechersAvailable = -1;
        }

        stat.availability = download.getStats().getAvailability();
        stat.shareRatio = download.getStats().getShareRatio() / 1000.0;
        stat.estimatedSecsToCompletion = download.getStats().getETASecs();

        stat.fileInfos = mediaServer.getContentUrls(download);

        return stat;
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

}
