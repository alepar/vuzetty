package ru.alepar.vuzetty.integration;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.alepar.vuzetty.client.remote.StatsListener;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.common.api.Category;
import ru.alepar.vuzetty.common.api.DownloadStats;
import ru.alepar.vuzetty.common.api.TorrentInfo;
import ru.alepar.vuzetty.server.VuzettyServer;
import ru.alepar.vuzetty.server.vuze.TorrentApi;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static ru.alepar.vuzetty.integration.Support.sleep;

@RunWith(JMock.class)
public class ClientServerCommunicationTest {

    private static final InetSocketAddress LISTEN_ADDRESS = new InetSocketAddress("localhost", 32337);

    private static final byte[] TORRENT_ONE = new byte[]{0x03, 0x13, 0x37};
    private static final byte[] TORRENT_TWO = new byte[]{0x03, 0x13, 0x38};

    private static final String NICK_ONE = "NicknameForOne";
    private static final String NICK_TWO = "NicknameForTwo";

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void whenClientSubmitsTorrentServerReceivesItAndPassesItToVuze() throws Exception {
        final Category category = new Category(NICK_ONE);
        final TorrentApi api = mockery.mock(TorrentApi.class);

        mockery.checking(new Expectations() {{
            allowing(api).getHashesFor(category);
                will(returnValue(Collections.emptySet()));
            one(api).addTorrent(TORRENT_ONE, category);
        }});

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);

        try {
            client.addTorrent(TORRENT_ONE);
            sleep();
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

    @Test
    public void whenClientAddsTorrentItReceivesTorrentAddedEvent() throws Exception {
        final TorrentApi api = new MockTorrentApi();
        final TorrentListener torrentListener = new TorrentListener();

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);

        client.setTorrentListener(torrentListener);

        try {
            client.addTorrent(TORRENT_ONE);
            client.addTorrent(TORRENT_TWO);
            sleep();

            assertThat(torrentListener.hashesAdded.size(), equalTo(2));
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

    @Test
    public void whenAnotherClientWithTheSameNicknameAddsTorrentFirstClientReceivesTorrentAddedEventToo() throws Exception {
        final TorrentApi api = new MockTorrentApi();
        final TorrentListener torrentListener = new TorrentListener();

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient clientOne = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);
        final VuzettyClient clientTwo = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);

        clientTwo.setTorrentListener(torrentListener);

        try {
            sleep();
            clientOne.addTorrent(TORRENT_ONE);
            clientOne.addTorrent(TORRENT_TWO);
            sleep();

            assertThat(torrentListener.hashesAdded, hasSize(2));
        } finally {
            clientOne.shutdown();
            clientTwo.shutdown();
            server.shutdown();
        }
    }

    @Test
    public void clientsWithDifferentNicksDoNotReceiveTorrentChangeEventsFromEachOther() throws Exception {
        final TorrentApi api = new MockTorrentApi();
        final TorrentListener torrentListenerOne = new TorrentListener();
        final TorrentListener torrentListenerTwo = new TorrentListener();

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient clientOne = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);
        final VuzettyClient clientTwo = new VuzettyClient(LISTEN_ADDRESS, NICK_TWO);

        clientOne.setTorrentListener(torrentListenerOne);
        clientTwo.setTorrentListener(torrentListenerTwo);

        try {
            clientOne.addTorrent(TORRENT_ONE);
            clientTwo.addTorrent(TORRENT_TWO);
            sleep();

            assertThat(torrentListenerOne.hashesAdded.size(), equalTo(1));
            assertThat(torrentListenerTwo.hashesAdded.size(), equalTo(1));
        } finally {
            clientOne.shutdown();
            clientTwo.shutdown();
            server.shutdown();
        }
    }

    @Test
    public void whenClientPollsForTorrentItReceivesInfoForTorrentsAdded() throws Exception {
        final TorrentApi api = new MockTorrentApi();
        final SavingStatsListener statsListener = new SavingStatsListener();

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS, NICK_ONE);

        client.setStatsListener(statsListener);

        try {
            client.addTorrent(TORRENT_ONE);
            client.addTorrent(TORRENT_TWO);
            sleep();
            client.pollForStats();
            sleep();

            assertThat(statsListener.updatedStats.length, equalTo(2));
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

    private static class SavingStatsListener implements StatsListener {
        private DownloadStats[] updatedStats = new DownloadStats[0];
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            this.updatedStats = updatedStats;
        }
    }

    private static class TorrentListener implements ru.alepar.vuzetty.common.listener.TorrentListener {
        private final Set<TorrentInfo> hashesAdded = new HashSet<TorrentInfo>();
        @Override
        public void onTorrentAdded(TorrentInfo info) {
            hashesAdded.add(info);
        }
    }
}
