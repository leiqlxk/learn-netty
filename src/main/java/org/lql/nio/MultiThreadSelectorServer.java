package org.lql.nio;

import org.lql.nio.bytebuffer.ByteBufferDebugUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: MultiThreadSelectorServer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/10 21:59 <br>
 */
public class MultiThreadSelectorServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadSelectorServer.class);

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(9000));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        // 创建固定数量的worker，并初始化，至少设置为cpu核心数，才能充分发挥出机器的性能
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }

        AtomicInteger index = new AtomicInteger();
        while (true) {
            selector.select();

            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    LOGGER.debug("connected.... {}", sc.getRemoteAddress());

                    // 当有连接新建时放入其余线程，关联selector
                    LOGGER.debug("before register.... {}", sc.getRemoteAddress());
                   // round robin
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    LOGGER.debug("after register.... {}", sc.getRemoteAddress());

                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        // 还未初始化
        private volatile boolean start = false;

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                this.thread = new Thread(this, name);
                this.selector = Selector.open();
                thread.start();
                start = true;
            }

            // 简单方法，wakeup相当于手里拿了一张票只有在解除了一次阻塞才会收回这张票，所以无论select()的位置在哪都有效
             this.selector.wakeup();
             sc.register(this.selector, SelectionKey.OP_READ);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.selector.select();

                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                    if (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            LOGGER.debug("red.... {}", sc.getRemoteAddress());
                            sc.read(buffer);
                            buffer.flip();
                            ByteBufferDebugUtil.debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
