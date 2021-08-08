package org.lql.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Title: TestByteBufferMethod <br>
 * ProjectName: learn-netty <br>
 * description: bytebuffer常用方法 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 17:12 <br>
 */
public class TestByteBufferMethod {

    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd', 'e', 'f'});
        buffer.flip();

        buffer.get(new byte[4]);
        ByteBufferDebugUtil.debugAll(buffer);
        buffer.rewind();
        ByteBufferDebugUtil.debugAll(buffer);
        System.out.println((char) buffer.get());
        ByteBufferDebugUtil.debugAll(buffer);

        // mark & reset
        // mark做一个标记，记录position位置，reset是将position重置到mark位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark(); // 加标记
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        // get(i)不会改变读索引位置
        System.out.println((char) buffer.get(5));
        ByteBufferDebugUtil.debugAll(buffer);

        // 转换
        buffer.clear();
        buffer.put("hello".getBytes(StandardCharsets.UTF_8));
        ByteBufferDebugUtil.debugAll(buffer);
        // 此方法直接切换为读模式
        buffer = StandardCharsets.UTF_8.encode("hello");
        ByteBufferDebugUtil.debugAll(buffer);
        //此方法直接切换为读模式
        buffer.clear();
        buffer = ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8));
        ByteBufferDebugUtil.debugAll(buffer);

        String string = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(string);
        ByteBufferDebugUtil.debugAll(buffer);

        // 分散读取
        try (FileChannel channel = new RandomAccessFile("data.txt", "r").getChannel()) {
            ByteBuffer buffer1 = ByteBuffer.allocate(3);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            ByteBuffer buffer3 = ByteBuffer.allocate(3);

            channel.read(new ByteBuffer[]{buffer1, buffer2, buffer3});
            buffer1.flip();
            buffer2.flip();
            buffer3.flip();
            ByteBufferDebugUtil.debugAll(buffer1);
            ByteBufferDebugUtil.debugAll(buffer2);
            ByteBufferDebugUtil.debugAll(buffer3);
        } catch (IOException e) {
        }

        // 集中写
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel chanel = new RandomAccessFile("data.txt", "rw").getChannel()) {
            chanel.write(new ByteBuffer[]{buffer1, buffer2, buffer3});
        } catch (IOException e) {
        }
    }
}
