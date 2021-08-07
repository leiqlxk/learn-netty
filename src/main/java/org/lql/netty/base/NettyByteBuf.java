package org.lql.netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Title: NettyByteBuf <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/7 14:53 <br>
 */
public class NettyByteBuf {

    public static void main(String[] args) {
        // 创建byteBuf对象，该对象内部包含一个字节数组byte[10]
        // 即通过readerIndex和writerIndex和capacity将buffer分成三个区域
        // 已经读取的区域：[0, readIndex）
        // 可读取的区域：[readIndex, writerIndex）
        // 可写的区域：[writerIndex, capacity]
        ByteBuf buf = Unpooled.buffer(10);
        System.out.println("byteBuf=" + buf);

        for (int i = 0; i < 8; i++) {
            buf.writeByte(i);
        }
        System.out.println("byteBuf=" + buf);

        for (int i = 0; i < 5; i++) {
            System.out.println(buf.getByte(i));
        }
        System.out.println("byteBuf=" + buf);

        for (int i = 0; i < 5; i++) {
            System.out.println(buf.readByte());
        }
        System.out.println("byteBuf=" + buf);

        // 用Unpooled工具类创建ByteBuf
        ByteBuf buf1 = Unpooled.copiedBuffer("hello, curry", CharsetUtil.UTF_8);

        // 使用相关的方法
        if (buf1.hasArray()) {
            byte[] context = buf1.array();
            // 将context转成字符串
            System.out.println(new String(context, CharsetUtil.UTF_8));
            System.out.println("buf1=" + buf1);

            // 获取数组0这个位置的字符h的ascii码，h=104
            System.out.println(buf1.getByte(0));

            // 可读字节数12
            int len = buf1.readableBytes();
            System.out.println("len=" + len);

            // 使用for去除各个字节
            for (int i = 0; i < len; i++) {
                System.out.println((char) buf1.getByte(i));
            }

            // 范围读取
            System.out.println(buf1.getCharSequence(0, 6, CharsetUtil.UTF_8));
            System.out.println(buf1.getCharSequence(6, 6, CharsetUtil.UTF_8));

        }
    }
}
