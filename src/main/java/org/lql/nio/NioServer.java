package org.lql.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Title: NioServer <br>
 * ProjectName: learn-netty <br>
 * description: 基础非阻塞io <br>
 * 此模式虽然单线程可以处理成千上万个客户端，但循环过程效率太低，连接越多性能越低，存在过多空转有性能瓶颈
 *
 * jdk早期的select、poll类似于此操作，只是select的线程集合有个数限制（1024），而poll的没有限制
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/6 21:40 <br>
 */
public class NioServer {

    // 保存客户端连接
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = null;
        try {
            // 创建NIO ServerSocketChannel与BIO的ServerSocket类似
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9000));

            // 设置ServerSocketChannel为非阻塞
            serverSocketChannel.configureBlocking(false);
            System.out.println("服务启动成功");

            while (true) {
                // 非阻塞模式下accept方法不会阻塞
                // NIO的非阻塞是有操作系统内部实现的，底层调用linux内核的accept函数
                SocketChannel socketChannel = serverSocketChannel.accept();

                // 如果有客户端进行连接
                if (socketChannel != null) {
                    System.out.println("连接成功");
                    // 设置SocketChannel为非阻塞
                    socketChannel.configureBlocking(false);
                    // 保存客户端连接在List中
                    channelList.add(socketChannel);
                }

                // 遍历连接进行数据读取
                Iterator<SocketChannel> iterator = channelList.iterator();
                while (iterator.hasNext()) {
                    SocketChannel sc = iterator.next();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);

                    // 非阻塞模式read方法不会阻塞
                    int len = sc.read(byteBuffer);

                    // 如果有数据，打印
                    if (len > 0) {
                        System.out.println("接收到消息：" + new String(byteBuffer.array()));
                    }else if (len == -1) {
                        iterator.remove();
                        System.out.println("客户端断开连接");
                    }
                }
            }
        }finally {
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
            }
        }

    }
}
