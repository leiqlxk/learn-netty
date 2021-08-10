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

/**
 * Title: MultiThreadSelectorServer <br>
 * ProjectName: learn-netty <br>
 * description: 多线程优化版 <br>
 * 使用单线程时nio虽然可以处理很多事件，但当有某个事件耗时较长，则会影响别的事件的处理，多线程可以更合理的使用cpu，但要合理的控制线程数
 *
 * 主线程负责监听accept，其余线程负责处理读写
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/10 21:11 <br>
 */
public class DoubleThreadSelectorServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleThreadSelectorServer.class);

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(9000));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        // 创建固定数量的worker
        Worker worker = new Worker("worker-0");
        // worker.register();

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
                    // 把worker.register();放在前一行不一定就先执行，因为此行是在子线程运行，而下一行运行在主线程，但是第一次成功了之后，第二次有新的客户端连进来，此selector又进入阻塞了，因此不能解决问题
                    // worker.register();
                    // 当同时使用一个selector时，先使用了selector.select()阻塞住了就会把register也阻塞掉
                    // sc.register(worker.selector, SelectionKey.OP_READ);
                    // 通过把channel传入子线程来让两者在同一个线程执行
                    worker.register(sc);
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
        // 通过此线程安全的队列来让连个线程通信
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

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
            queue.add(() -> {
                try {
                    sc.register(this.selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });

            // 主动唤醒selector
            this.selector.wakeup();

            // 简单方法，wakeup相当于手里拿了一张票只有在解除了一次阻塞才会收回这张票，所以无论select()的位置在哪都有效
            // this.selector.wakeup();
            // sc.register(this.selector, SelectionKey.OP_READ);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.selector.select();

                    // 取出任务
                    Runnable taks = queue.poll();
                    if (taks != null) {
                        // 执行sc.register(this.selector, SelectionKey.OP_READ);任务
                        taks.run();
                    }

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
