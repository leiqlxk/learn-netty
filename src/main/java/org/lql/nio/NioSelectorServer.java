package org.lql.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Title: NioSelectorServer <br>
 * ProjectName: learn-netty <br>
 * description: 同步非阻塞，服务器实现模式为一个线程可以处理多个请求，客户端发送的连接请求都会注册到多路复用器selector上，
 * 多路复用器轮询到连接有IO请求就进行处理，jdk1.4开始引入使用Linux内核函数select()或poll实现，从1.5开始引入epoll基于事件
 * 响应机制来优化的NIO
 * redis也是使用epoll模型才实现了单线程的高并发<br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/6 22:13 <br>
 */
public class NioSelectorServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);

        // 打开Selector处理Channel，即创建epoll
        Selector selector = Selector.open();
        // 把ServerSocketChannel注册到Selector上，并且Selector对客户端accept连接操作感兴趣
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");

        while (true) {
            // 阻塞等待需要处理的事件发生
            selector.select();

            // 获取selector中注册的全部事件的SelectionKey实例，当同时触发事件太多时，此模式还是会有轮询处理时间太久的问题
            // 此时可以考虑使用线程池来进行相应的业务处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // 遍历selectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if(key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);

                    // 这里注册读事件，当客户端发送数据时会触发这个channel的读事件，要给客户端发送数据可以注册写事件
                    SelectionKey selectionKey1 = socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                }else if (key.isReadable()) {// 如果是读事件
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);

                    int len = socketChannel.read(byteBuffer);
                    if (len > 0) {
                        System.out.println("接收到数据：" + new String(byteBuffer.array()));
                    }else if (len == -1) {
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }

                // 从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }
        }
    }
}
