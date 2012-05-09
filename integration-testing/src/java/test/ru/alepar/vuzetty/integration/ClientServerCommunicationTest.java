package ru.alepar.vuzetty.integration;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.alepar.vuzetty.api.DownloadStats;
import ru.alepar.vuzetty.client.remote.StatsListener;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.server.VuzettyServer;
import ru.alepar.vuzetty.server.api.TorrentApi;

import java.net.InetSocketAddress;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.alepar.vuzetty.integration.Support.sleep;

@RunWith(JMock.class)
public class ClientServerCommunicationTest {

    private static final InetSocketAddress LISTEN_ADDRESS = new InetSocketAddress("localhost", 31337);

    private static final byte[] TORRENT_ONE = new byte[]{0x03, 0x13, 0x37};
    private static final byte[] TORRENT_TWO = new byte[]{0x03, 0x13, 0x38};

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void whenClientSubmitsTorrentServerReceivesItAndPassesItToVuze() throws Exception {
        final TorrentApi api = mockery.mock(TorrentApi.class);

        mockery.checking(new Expectations() {{
            one(api).addTorrent(TORRENT_ONE);
        }});

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS);

        try {
            client.addTorrent(TORRENT_ONE);
            sleep();
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

    @Test
    public void whenClientPollsForDataItReceivesBackDataForAlltorentsItHasAdded() throws Exception {
        final TorrentApi api = new MockTorrentApi();
        final SavingListener listener = new SavingListener();

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS);

        client.setStatsListener(listener);

        try {
            client.addTorrent(TORRENT_ONE);
            client.addTorrent(TORRENT_TWO);
            client.pollForStats();
            sleep();

            assertThat(listener.updatedStats.length, equalTo(2));
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

    private static class SavingListener implements StatsListener {
        private DownloadStats[] updatedStats = new DownloadStats[0];
        @Override
        public void onStatsUpdate(DownloadStats[] updatedStats) {
            this.updatedStats = updatedStats;
        }
    }
}
