package org.lql.nio.bytebuffer;

import java.nio.ByteBuffer;

/**
 * Title: TestByteBufferReadWrite <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 16:59 <br>
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        ByteBufferDebugUtil.debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        ByteBufferDebugUtil.debugAll(buffer);
        // flip之前读不到东西
//        System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        ByteBufferDebugUtil.debugAll(buffer);
        buffer.compact();
        ByteBufferDebugUtil.debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x66});
        ByteBufferDebugUtil.debugAll(buffer);
    }
}
