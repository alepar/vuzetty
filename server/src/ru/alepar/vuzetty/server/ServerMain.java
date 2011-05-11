package ru.alepar.vuzetty.server;

import ru.alepar.rpc.RpcServer;
import ru.alepar.rpc.netty.NettyRpcServer;
import ru.alepar.vuzetty.api.TorrentApi;
import ru.alepar.vuzetty.server.api.VuzeTorrentApi;

import java.net.InetSocketAddress;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new NettyRpcServer(new InetSocketAddress(31337));
        rpcServer.addImplementation(TorrentApi.class, new VuzeTorrentApi());
    }
}
