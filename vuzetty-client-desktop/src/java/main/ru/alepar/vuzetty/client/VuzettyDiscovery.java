package ru.alepar.vuzetty.client;

import ru.alepar.rpc.api.NettyRpcClientBuilder;
import ru.alepar.rpc.api.NettyRpcServerBuilder;
import ru.alepar.rpc.api.RpcClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;

import java.net.InetSocketAddress;

public class VuzettyDiscovery {

    private static final InetSocketAddress DISCOVERY_ADDRESS = new InetSocketAddress("localhost", 31336);

    public VuzettyRemote discover() {
        try {
            final RpcClient client = new NettyRpcClientBuilder(DISCOVERY_ADDRESS).build();
            return client.getRemote().getProxy(VuzettyRemote.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void announce(VuzettyRemote vuzetty) {
        new NettyRpcServerBuilder(DISCOVERY_ADDRESS)
                .addObject(VuzettyRemote.class, vuzetty)
                .build();
    }

}
