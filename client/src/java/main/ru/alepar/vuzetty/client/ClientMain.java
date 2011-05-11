package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.rpc.RpcClient;
import ru.alepar.rpc.netty.NettyRpcClient;
import ru.alepar.vuzetty.api.TorrentApi;

import java.io.*;
import java.net.InetSocketAddress;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        RpcClient rpcClient = new NettyRpcClient(new InetSocketAddress("alepar.ru", 31337));
        try {
            TorrentApi api = rpcClient.getImplementation(TorrentApi.class);
            String hash = api.addTorrent(readFile(args[0]));
        } finally {
            rpcClient.shutdown();
        }
    }

    private static byte[] readFile(String arg) throws IOException {
        InputStream is = new FileInputStream(new File(arg));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[10240];
        int read;
        while((read = is.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        return bos.toByteArray();
    }
}
