package org.lql.netty.eventloop;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Title: TestEventLoop <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 19:40 <br>
 */
public class TestEventLoop {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventLoop.class);

    public static void main(String[] args) {
        // 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2); // 可以处理IO事件、普通任务、定时任务
//        EventLoopGroup group1 = new DefaultEventLoop(); // 只能处理普通任务、定时任务

        // 获取下一个事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 执行普通任务，比如比较耗时的任务就可以交给它去进行异步处理
//        group.next().execute()
        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.debug("ok");
        });

        // 执行定时任务 参数 runable对象 初始延迟时间 间隔时间  时间单位
        group.next().scheduleAtFixedRate(() -> {
            LOGGER.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);

        LOGGER.debug("main");
    }
}
