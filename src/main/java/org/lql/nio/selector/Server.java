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
import java.nio.charset.Charset;
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

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);

        socketChannel.bind(new InetSocketAddress(9000));
        // 2. 将socketChannel注册到Selector上
        SelectionKey sscKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        LOGGER.debug("服务端已启动.....");
        while (true) {
            // 阻塞，直到epoll_wait返回，且在有事件未处理或key.cancel()取消事件时它也不会阻塞
            selector.select();

            // 获取所有有事件触发的key集合
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 处理完事件后移除，防止再次处理，因为处理完事件之后只会移除事件，但是不会移除key
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) sscKey.channel();
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);

                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 通过附件的方式给channel绑定buffer用于解决报文超过buffer容量的边界问题
                    channel.register(selector, SelectionKey.OP_READ, buffer);
                    LOGGER.debug("客户端已连接... {}", channel);
                } else if (key.isReadable()) {
                    // 拿到触发事件的channel
                    SocketChannel channel = (SocketChannel) key.channel();

                    // 测试边界处理问题
                    // ByteBuffer buffer = ByteBuffer.allocate(4);

                    // 通过获取附件获取channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    try {
                        int len = channel.read(buffer);
                        if (len > 0) {
                            // buffer.flip();
                            // LOGGER.debug("接收到客户端消息 {}", Charset.defaultCharset().decode(buffer).toString());
                            // ByteBufferDebugUtil.debugRead(buffer);
                            split(buffer); // 边界处理

                            // 当buffer的当前位置和写限制相等时说明已满
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                // 使用扩容后的buffer来替换旧的buffer
                                key.attach(newBuffer);
                            }
                        }else if(len == -1) {// 客户端正常断开,read方法返回-1
                            LOGGER.debug("客户端断开连接{}", channel);
                            channel.close();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();// 客户端异常断开也会产生一个读事件，但是read会报错，key.cancel()方法会将key从selector的keys集合中真正删除
                    }
                }

            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();

        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                // 把完整消息存入新的ByteBuffer
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);

                // 从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                ByteBufferDebugUtil.debugAll(target);
            }
        }

        // 没处理完的留着跟下次的合并再处理
        source.compact();
    }
}
