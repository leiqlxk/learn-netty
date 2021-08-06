package org.lql.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Title: ServerSocket <br>
 * ProjectName: learn-netty <br>
 * description: 阻塞io <br>
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
