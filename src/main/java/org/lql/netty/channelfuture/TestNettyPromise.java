package org.lql.netty.channelfuture;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Title: TestNettyPromise <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 21:52 <br>
 */
public class TestNettyPromise {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestNettyFuture.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 准备Eventloop
        EventLoop eventLoop = new NioEventLoopGroup().next();

        // 可以主动创建 promise，实际就是一个结果容器，任何线程都可以往里面写入结果
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);


        new Thread(() -> {
            // 任意一个线程执行计算，计算完毕后向promise填充结果
            LOGGER.debug("开始计算....");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            promise.setSuccess(80);
        }).start();

        // 接收结果
        LOGGER.debug("等待结果....");
        LOGGER.debug("结果是：{}", promise.get());
    }
}
