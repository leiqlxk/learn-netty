package org.lql.nio.bytebuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Title: TestByteBuffer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 15:18 <br>
 */
public class TestByteBuffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestByteBuffer.class);

    public static void main(String[] args) {
        // FileChannel
        // 1.输入/输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //  从channel读取数据，向buffer写入
                int len = channel.read(buffer);
                LOGGER.debug("读取到的字节长度为：{}", len);

                // 没有内容
                if (len == -1) {
                    break;
                }
                // 打印buffer内容
                buffer.flip(); // 切换到buffer的读模式
                while (buffer.hasRemaining()) { // 是否还有剩余未读数据
                    byte b = buffer.get();
                    LOGGER.debug("读取到的字节：{}", (char) b);
                }

                // 切换为写模式
                buffer.clear();
            }

        } catch (IOException e) {
        }
    }
}
