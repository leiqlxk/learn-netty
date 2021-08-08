package org.lql.nio.selector;

import org.lql.nio.bytebuffer.ByteBufferDebugUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Title: Server <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 22:02 <br>
 */
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        // 1. 创建selector，管理多个channel，无论serverSocketChannel还是SocketChannel都要注册到Selector上
        Selector selector = Selector.open();

        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);

        socketChannel.bind(new InetSocketAddress(9000));
        // 2. 将socketChannel注册到Selector上
        SelectionKey sscKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        LOGGER.debug("服务端已启动.....");
        while (true) {
            // 阻塞，直到epoll_wait返回
            selector.select();

            // 获取所有有事件触发的key集合
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) sscKey.channel();
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);

                    channel.register(selector, SelectionKey.OP_READ);
                    LOGGER.debug("客户端已连接... {}", channel);
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    int len = channel.read(buffer);
                    if (len > 0) {
                        LOGGER.debug("接收到客户端消息 {}", new String(buffer.array()));
                        buffer.flip();

                        ByteBufferDebugUtil.debugRead(buffer);
                    }else if(len == -1) {
                        LOGGER.debug("客户端断开连接{}", channel);
                        socketChannel.close();
                    }

                }
                // 处理完事件后移除，防止再次处理
                iterator.remove();
            }
        }
    }
}
