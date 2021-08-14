package org.lql.netty.chatandrpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.lql.netty.chatandrpc.config.Config;
import org.lql.netty.chatandrpc.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Title: MessageCodecSharable <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 * 必须前置 LengthFixedBasedFrameDecoder配合使用， 保证解码方法获取到的消息是一条完整的消息
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:03 <br>
 */
// 用于是否可以被共用，即无状态信息保存的handler
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCodecSharable.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        // 4字节的魔数
        byteBuf.writeBytes(new byte[]{'l', 'q', 'l', 'n'});

        // 1 字节版本号
        byteBuf.writeByte(1);

        // 1 字节序列化算法 0-jdk 1-json
        byteBuf.writeByte(Config.getSerializerAlgorithm().ordinal());

        // 1 字节指令类型
        byteBuf.writeByte(message.getMessageType());

        // 4 字节指令序号
        byteBuf.writeInt(message.getSequenceId());

        //无意义，对齐填充 一般固定长度的字节为2整数倍
        byteBuf.writeByte(0xff);

        // 获取内容的字节数组
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes = bos.toByteArray();*/
//        byte[] bytes = Serializer.Algorithm.Java.serialize(message);
        byte[] bytes = Config.getSerializerAlgorithm().serialize(message);


        // 4 字节正文长度
        byteBuf.writeInt(bytes.length);

        // 写入内容
        byteBuf.writeBytes(bytes);

        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();

        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);

        // 获取反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializeType];
        // 找到对应的消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
       /* if (serializeType == 0) {
           *//* ObjectInputStream osi = new ObjectInputStream(new ByteArrayInputStream(bytes));
            message = (Message) osi.readObject();*//*
//            message = Serializer.Algorithm.Java.deserialize(Message.class, bytes);
        }*/



        LOGGER.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializeType, messageType, sequenceId, length);
        LOGGER.debug("{}", message);

        // 将消息添加到上下文，否则下一个handler无法获取
        list.add(message);
    }
}
