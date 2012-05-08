package ru.alepar.vuzetty.integration;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.alepar.vuzetty.api.ServerApi;
import ru.alepar.vuzetty.client.VuzettyClient;
import ru.alepar.vuzetty.server.VuzettyServer;

import java.net.InetSocketAddress;

import static ru.alepar.vuzetty.integration.Support.sleep;

@RunWith(JMock.class)
public class ClientServerCommunicationTest {

    private static final InetSocketAddress LISTEN_ADDRESS = new InetSocketAddress("localhost", 31337);
    private static final byte[] TORRENT = new byte[]{0x03, 0x13, 0x37};

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void whenClientSubmitsTorrentServerReceivesItAndPassesItToVuze() throws Exception {
        final ServerApi api = mockery.mock(ServerApi.class);

        mockery.checking(new Expectations() {{
            one(api).addTorrent(TORRENT);
        }});

        final VuzettyServer server = new VuzettyServer(LISTEN_ADDRESS, api);
        final VuzettyClient client = new VuzettyClient(LISTEN_ADDRESS);

        try {
            client.addTorrent(TORRENT);
            sleep();
        } finally {
            client.shutdown();
            server.shutdown();
        }
    }

}
