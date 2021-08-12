package org.lql.netty.advance.protocol.define;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.lql.netty.advance.protocol.define.message.LoginRequestMessage;
import org.lql.netty.advance.protocol.define.protocol.MessageCodec;

/**
 * Title: TestMessageCodec <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/12 23:45 <br>
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0,0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()
        );

        // encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123", "张三");
        channel.writeOutbound(loginRequestMessage);

        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, loginRequestMessage, buf);

        // 入站
        channel.writeInbound(buf);
    }
}
