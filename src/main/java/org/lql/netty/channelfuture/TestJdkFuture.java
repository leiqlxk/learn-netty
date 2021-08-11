package org.lql.netty.channelfuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Title: TestJdkFuture <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 21:34 <br>
 */
public class TestJdkFuture {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestJdkFuture.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // 提交任务
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                LOGGER.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });

        // 主线程来通过future获取结果
        LOGGER.debug("等待结果");
        LOGGER.debug("结果是 {}", future.get());

    }
}
