package org.lql.netty.chatandrpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chatandrpc.message.RpcRequestMessage;
import org.lql.netty.chatandrpc.message.RpcResponseMessage;
import org.lql.netty.chatandrpc.server.service.HelloService;
import org.lql.netty.chatandrpc.server.service.ServicesFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            HelloService service = (HelloService)
                    ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            response.setExceptionValue(new Exception("远程调用出错:" + msg));
        }
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "org.lql.netty.chatandrpc.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        HelloService service = (HelloService)
                ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
