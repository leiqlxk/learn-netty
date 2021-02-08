package org.lql.bioplus;

import org.lql.bio.TimeHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Title: TimeServer <br>
 * ProjectName: learn-netty <br>
 * description: 伪异步I/O服务端 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/2/8 13:21 <br>
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port:" + port);

            //创建一个时间服务器处理类的线程池，当接收到新的客户端连接时，将请求Scoket封装成一个Task，然后调用线程池的execute方法执行，从而避免了每个请求接入都创建一个新的线程
            TimeHandlerExecutePool timeHandlerExecutePool = new TimeHandlerExecutePool(50, 1000);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                timeHandlerExecutePool.execute(new TimeHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (server != null) {
                try {
                    System.out.println("The time server is close");
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
