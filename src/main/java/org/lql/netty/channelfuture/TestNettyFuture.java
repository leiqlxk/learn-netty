package org.lql.netty.channelfuture;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Title: Test <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 21:41 <br>
 */
public class TestNettyFuture {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestNettyFuture.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                LOGGER.debug("执行计算");
                Thread.sleep(1000);
                return 70;
            }
        });

        // 主线程来通过future获取结果
        LOGGER.debug("等待结果");
        // get方法阻塞等待
//        LOGGER.debug("结果是 {}", future.get());
        future.addListener(future1 -> LOGGER.debug("接收结果是 {}", future1.getNow()));
    }
}
