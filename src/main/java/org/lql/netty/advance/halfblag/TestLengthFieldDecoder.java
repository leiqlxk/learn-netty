package org.lql.netty.advance.halfblag;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * Title: LengthFieldClient <br>
 * ProjectName: learn-netty <br>
 * description: 基于长度字段解决黏包半包 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/12 21:42 <br>
 */
public class TestLengthFieldDecoder {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 基于长度字段的解码器
                // 参数1 最大帧长度
                // 参数1 长度字段起始偏移量
                // 参数3 长度字段字节数（几个字节用来记录长度）
                // 参数4 调整字段，即长度字节后几个字节之后才是真正的实际内容
                // 参数5 是否需要去掉头几个字节
//            new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
//            new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
            new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 4),
            new LoggingHandler(LogLevel.DEBUG)
        );

        // 4 个字节的内容长度  实际内容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        send(buf, "Hello, world");
        send(buf, "Hi");
        send(buf, "Hello, curry");

        channel.writeInbound(buf);
    }

    private static void send(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8); // 实际内容
        int len = bytes.length;// 实际长度
        buf.writeInt(len);
        buf.writeByte(1);
        buf.writeBytes(bytes);
    }
}
