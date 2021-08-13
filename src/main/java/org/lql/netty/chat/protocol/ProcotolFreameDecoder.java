package org.lql.netty.chat.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Title: ProcotolFreameDecoder <br>
 * ProjectName: learn-netty <br>
 * description: 协议定好后结构基本不会发生变化，可以抽出来封装使得server类使用方便  <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 23:10 <br>
 */
public class ProcotolFreameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFreameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFreameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
