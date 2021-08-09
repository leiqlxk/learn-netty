package org.lql.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Title: WriterServer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/9 22:48 <br>
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(9000));

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel sc = serverSocketChannel.accept();
                    sc.configureBlocking(false);
                    SelectionKey sckey = sc.register(selector, 0, null);
                    sckey.interestOps(SelectionKey.OP_READ);
                    // 模拟发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // write方法并不能保证一次写完，返回值代表实际写入的字节数
                    /*while (buffer.hasRemaining()) {
                        int len = sc.write(buffer);
                        System.out.println(len);
                    }*/
                    // 当数据量很大的时候一次并发不完，所以会一直在这循环，并且在网络缓冲区写满时，循环进来也写不进任何东西，因此会造成效率问题，我们可以再此时让其先去关注下别的事件
                    if (buffer.hasRemaining()) {
                        // 关注可写事件，但是不能覆盖上面关注的可读时间
                        sckey.interestOps(sckey.interestOps() + SelectionKey.OP_WRITE);
                        // 把未写完的数据挂到key上
                        sckey.attach(buffer);
                    }

                }else if (key.isWritable()) {
                    // 将while换成此种方式可以使用事件触发再一次进来，就可以让线程先去处理其他事件
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 清理操作
                    if (!buffer.hasRemaining()) {
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
