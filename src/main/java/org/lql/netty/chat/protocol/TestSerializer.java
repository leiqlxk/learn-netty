package org.lql.netty.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.lql.netty.chat.message.LoginRequestMessage;

/**
 * Title: TestSerializer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 9:31 <br>
 */
public class TestSerializer {

    public static void main(String[] args) throws Exception {
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

        EmbeddedChannel channel = new EmbeddedChannel(
                loggingHandler, messageCodecSharable, loggingHandler
        );

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);

        // 编码
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);

        // 入站
        channel.writeInbound(buf);
    }
}
