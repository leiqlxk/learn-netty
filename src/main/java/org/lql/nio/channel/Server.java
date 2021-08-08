package org.lql.nio.channel;

import org.lql.nio.bytebuffer.ByteBufferDebugUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.WildcardType;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: Server <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 21:05 <br>
 */
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * 此模式虽然单线程可以处理成千上万个客户端，但循环过程效率太低，连接越多性能越低，存在过多空转有性能瓶颈
     * jdk早期的select、poll类似于次操作，只是select的线材集合有个数限制（1024），而poll的没有限制
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // 非阻塞模式
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建服务器，ServerSocketChannel默认为阻塞模式
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 设置Channel为非阻塞
        ssc.configureBlocking(false);

        // 2. 绑定端口
        ssc.bind(new InetSocketAddress(9000));

        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 3. accept建立与客户端连接，socketChannel来与客户端之间通信
            LOGGER.debug("connecting......");
            // 非阻塞
            SocketChannel socketChannel = ssc.accept();

            LOGGER.debug("connected..... {}", socketChannel);
            if (socketChannel != null) {
                // 设置channel为非阻塞
                socketChannel.configureBlocking(false);
                channels.add(socketChannel);
            }

            // 4. 接收客户端发送的数据
            for (SocketChannel channel : channels) {
                LOGGER.debug("before read..... {}", channel);
                // 非阻塞
                channel.read(buffer);
                buffer.flip();
                ByteBufferDebugUtil.debugRead(buffer);
                buffer.clear();
                LOGGER.debug("after read..... {}", channel);
            }
        }
    }

    /**
     * 1. 阻塞模式下accept和read操作都是阻塞操作，如果没有接收到连接或者没有收到客户端发送的数据就会阻塞等待，一个线程就只能做一个事
     * 2. 加入多线程可以解决阻塞的问题，但是如果线程太多，内存占用搞会导致服务器压力太大，线程上下文切换成本高，多线程也只适合连接数少的场景
     * 3. 线程池版虽然解决了线程过多的问题，但是阻塞模式下，线程仅能处理一个socket连接，因此仅适合短连接场景
     * @param args
     * @throws IOException
     */
    public static void blocking(String[] args) throws IOException {
        // 理解nio阻塞模式

        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建服务器，ServerSocketChannel默认为阻塞模式
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 2. 绑定端口
        ssc.bind(new InetSocketAddress(9000));

        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 3. accept建立与客户端连接，socketChannel来与客户端之间通信
            LOGGER.debug("connecting......");
            // 阻塞
            SocketChannel socketChannel = ssc.accept();
            LOGGER.debug("connected..... {}", socketChannel);
            channels.add(socketChannel);

            // 4. 接收客户端发送的数据
            for (SocketChannel channel : channels) {
                LOGGER.debug("before read..... {}", channel);
                // 阻塞
                channel.read(buffer);
                buffer.flip();
                ByteBufferDebugUtil.debugRead(buffer);
                buffer.clear();
                LOGGER.debug("after read..... {}", channel);
            }
        }
    }
}
