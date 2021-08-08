package org.lql.nio.channel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Title: TestFileWalkFileTree <br>
 * ProjectName: learn-netty <br>
 * description: 遍历目录文件 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/8 20:26 <br>
 */
public class TestFileWalkFileTree {

    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Paths.get("D:\\Redis-x64-3.0.504"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=====>" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                return super.visitFile(file, attrs);
            }
        });
    }
}
