package ru.alepar.vuzetty.server;

import ru.alepar.rpc.api.NettyRpcServerBuilder;
import ru.alepar.rpc.api.RpcServer;
import ru.alepar.vuzetty.api.ServerApi;

import java.net.InetSocketAddress;

public class VuzettyServer {

    private final RpcServer rpc;

    public VuzettyServer(InetSocketAddress listenAddress, ServerApi api) {
        rpc = new NettyRpcServerBuilder(listenAddress)
                .addObject(ServerApi.class, api)
                .build();
    }

    public void shutdown() {
        rpc.shutdown();
    }

}
