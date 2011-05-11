package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.RpcClient;
import ru.alepar.rpc.netty.NettyRpcClient;
import ru.alepar.vuzetty.api.TorrentApi;

import java.net.InetSocketAddress;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        RpcClient rpcClient = new NettyRpcClient(new InetSocketAddress(31337));

        try {
            TorrentApi api = rpcClient.getImplementation(TorrentApi.class);
            boolean b = api.addTorrent(new byte[102400]);
            log.debug("" + b);
        } finally {
            rpcClient.shutdown();
        }
    }
}
