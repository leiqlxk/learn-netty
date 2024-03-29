package org.lql.nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Title: Client <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 21:18 <br>
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost", 9000));

        SocketAddress address = socketChannel.getLocalAddress();

        // 处理边界问题使用
        socketChannel.write(Charset.defaultCharset().encode("0123456789abcdef123456\nworld\n"));
        System.out.println("waiting....");

    }
}
