package org.lql.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Title: ServerSocket <br>
 * ProjectName: learn-netty <br>
 * description: 同步阻塞模型，一个客户端连接对应一个处理线程 <br>
 * 1. IO代码里read操作是阻塞操作，如果连接不做数据读写操作会导致线程阻塞
 * 2. 如果线程太多，内存占用搞会导致服务器压力太大，线程上下文切换成本高，多线程也只适合连接数少的场景
 * 3. 线程池版虽然解决了线程过多的问题，但是阻塞模式下，线程仅能处理一个socket连接，因此仅适合短连接场景
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/6 21:17 <br>
 */
public class SocketServer {

    public static void main(String[] args){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9000);

            while (true) {
                // 阻塞方法
                Socket socket = serverSocket.accept();
                System.out.println("有客户端连接");
                handler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    serverSocket = null;
                }

            }
        }

    }

    private static void handler(Socket socket) throws IOException {
        byte[] bytes = new byte[1024];

        System.out.println("准备read");
        // 接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = socket.getInputStream().read(bytes);
        System.out.println("read完毕");
        if (read != -1) {
            System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
        }
    }
}
