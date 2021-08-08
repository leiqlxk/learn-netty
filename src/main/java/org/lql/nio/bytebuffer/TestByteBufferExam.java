package org.lql.nio.bytebuffer;

import sun.security.util.Length;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;

/**
 * Title: TestByteBufferExam <br>
 * ProjectName: learn-netty <br>
 * description: 黏包半包 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 19:04 <br>
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI`m zhangsan\nHo".getBytes(StandardCharsets.UTF_8));
        split(source);
        source.put("w are you?\n".getBytes(StandardCharsets.UTF_8));
        split(source);
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
