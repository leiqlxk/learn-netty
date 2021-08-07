package org.lql.netty.directbuffer;

import java.nio.ByteBuffer;

/**
 * Title: DirectMemeryTest <br>
 * ProjectName: learn-netty <br>
 * description: 直接内存(即非jvm管理内存)测试 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/7 16:55 <br>
 */
public class DirectMemeryTest {

    public static void main(String[] args) {
        heapAccess();
        System.out.println("==============");
        directAccess();
    }

    // 访问堆内存
    public static void heapAccess() {
        long startTime = System.currentTimeMillis();

        // 分配堆内存
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();

            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }

            buffer.clear();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("堆内存访问：" + (endTime - startTime) + "ms");
    }

    // 访问直接内存
    public static void directAccess() {
        long startTime = System.currentTimeMillis();

        // 分配直接内存
        ByteBuffer buffer = ByteBuffer.allocateDirect(1000);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();

            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }

            buffer.clear();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("直接内存访问：" + (endTime - startTime) + "ms");
    }
}
