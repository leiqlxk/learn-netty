package org.lql.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);

            Socket socket = null;
            System.out.println("The time server is start in port:" + port);

            //通过一个无限循环来监听客户端连接，如果没有客户端接入则主线程阻塞在ServerSocket的accept操作上。当有新客户端接入时，启用一个新线程来处理这条Socket链路
            while (true) {
                socket = serverSocket.accept();
                new Thread(new TimeHandler(socket)).start();
            }
        }finally {
            if (serverSocket != null) {
                System.out.println("The time server close");
                serverSocket.close();
                serverSocket = null;
            }
        }
    }
}
