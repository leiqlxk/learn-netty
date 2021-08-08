package org.lql.nio.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Title: Test <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 19:50 <br>
 */
public class TestFileChannelTranferTo {

    public static void main(String[] args) {
        try (
            FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            long size = from.size();
            // left 变量代表还剩余多少字节
            for (long left = size; left > 0;) {
                left -= from.transferTo((size - left), left, to);
            }

            Path path = Paths.get("logback.xml");
            System.out.println(Files.exists(path));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
