package org.lql.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Title: TestSlice <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 23:28 <br>
 */
public class TestSlice {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);

        buf.writeBytes(new byte[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        TestByteBuf.log(buf);

        // 在切片过程中，并没有发生数据复制
        // 切片后的f1，f2不能在扩容，因为它扩容后就会影响别的切面的内容
        ByteBuf f1 = buf.slice(0, 5);
        f1.retain();
        ByteBuf f2 = buf.slice(5, 5);
        f2.retain();
        TestByteBuf.log(f1);
        TestByteBuf.log(f2);

        f1.setByte(2, 'z');
        TestByteBuf.log(f1);
        TestByteBuf.log(buf);

        f1.release();
        f2.release();
        // 原始的buf释放后，切片后的buf就不能在使用了，要用retain来加一。不让原始内存释放
       /* buf.release();
        TestByteBuf.log(f1);
        TestByteBuf.log(f2);*/
    }
}
