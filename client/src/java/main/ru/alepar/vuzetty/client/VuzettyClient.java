package ru.alepar.vuzetty.client;

import ru.alepar.rpc.api.NettyRpcClientBuilder;
import ru.alepar.rpc.api.RpcClient;
import ru.alepar.vuzetty.api.ServerApi;

import java.net.InetSocketAddress;

public class VuzettyClient implements ServerApi {

    private final RpcClient rpc;
    private final ServerApi api;

    public VuzettyClient(InetSocketAddress bindAddress) {
        rpc = new NettyRpcClientBuilder(bindAddress).build();
        api = rpc.getRemote().getProxy(ServerApi.class);
    }

    @Override
    public void addTorrent(byte[] torrent) {
        api.addTorrent(torrent);
    }

    @Override
    public void pollForStats() {
        api.pollForStats();
    }

    public void shutdown() {
        rpc.shutdown();
    }
}
