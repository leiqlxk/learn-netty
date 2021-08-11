package org.lql.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * Title: TestCompositeByteBuf <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 23:37 <br>
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();

        buf1.writeBytes(new byte[] {1, 2, 3, 4, 5});
        buf1.writeBytes(new byte[] {6, 7, 8, 9, 10});

        // 会发生拷贝
        /*ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(buf1).writeBytes(buf2);*/

        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf.addComponents(true, buf1, buf2);
        TestByteBuf.log(buf);

        buf.setByte(2, 12);
        TestByteBuf.log(buf);
        TestByteBuf.log(buf1);


    }
}
