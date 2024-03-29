package org.lql.netty.chatandrpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;

/**
 * Title: TestClod <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/25 14:46 <br>
 */
public class TestClod {
    public static int[] CRC8_TAB = {
            0x00, 0x07, 0x0E, 0x09, 0x1C, 0x1B, 0x12, 0x15,
            0x38, 0x3F, 0x36, 0x31, 0x24, 0x23, 0x2A, 0x2D,
            0x70, 0x77, 0x7E, 0x79, 0x6C, 0x6B, 0x62, 0x65,
            0x48, 0x4F, 0x46, 0x41, 0x54, 0x53, 0x5A, 0x5D,
            0xE0, 0xE7, 0xEE, 0xE9, 0xFC, 0xFB, 0xF2, 0xF5,
            0xD8, 0xDF, 0xD6, 0xD1, 0xC4, 0xC3, 0xCA, 0xCD,
            0x90, 0x97, 0x9E, 0x99, 0x8C, 0x8B, 0x82, 0x85,
            0xA8, 0xAF, 0xA6, 0xA1, 0xB4, 0xB3, 0xBA, 0xBD,
            0xC7, 0xC0, 0xC9, 0xCE, 0xDB, 0xDC, 0xD5, 0xD2,
            0xFF, 0xF8, 0xF1, 0xF6, 0xE3, 0xE4, 0xED, 0xEA,
            0xB7, 0xB0, 0xB9, 0xBE, 0xAB, 0xAC, 0xA5, 0xA2,
            0x8F, 0x88, 0x81, 0x86, 0x93, 0x94, 0x9D, 0x9A,
            0x27, 0x20, 0x29, 0x2E, 0x3B, 0x3C, 0x35, 0x32,
            0x1F, 0x18, 0x11, 0x16, 0x03, 0x04, 0x0D, 0x0A,
            0x57, 0x50, 0x59, 0x5E, 0x4B, 0x4C, 0x45, 0x42,
            0x6F, 0x68, 0x61, 0x66, 0x73, 0x74, 0x7D, 0x7A,
            0x89, 0x8E, 0x87, 0x80, 0x95, 0x92, 0x9B, 0x9C,
            0xB1, 0xB6, 0xBF, 0xB8, 0xAD, 0xAA, 0xA3, 0xA4,
            0xF9, 0xFE, 0xF7, 0xF0, 0xE5, 0xE2, 0xEB, 0xEC,
            0xC1, 0xC6, 0xCF, 0xC8, 0xDD, 0xDA, 0xD3, 0xD4,
            0x69, 0x6E, 0x67, 0x60, 0x75, 0x72, 0x7B, 0x7C,
            0x51, 0x56, 0x5F, 0x58, 0x4D, 0x4A, 0x43, 0x44,
            0x19, 0x1E, 0x17, 0x10, 0x05, 0x02, 0x0B, 0x0C,
            0x21, 0x26, 0x2F, 0x28, 0x3D, 0x3A, 0x33, 0x34,
            0x4E, 0x49, 0x40, 0x47, 0x52, 0x55, 0x5C, 0x5B,
            0x76, 0x71, 0x78, 0x7F, 0x6A, 0x6D, 0x64, 0x63,
            0x3E, 0x39, 0x30, 0x37, 0x22, 0x25, 0x2C, 0x2B,
            0x06, 0x01, 0x08, 0x0F, 0x1A, 0x1D, 0x14, 0x13,
            0xAE, 0xA9, 0xA0, 0xA7, 0xB2, 0xB5, 0xBC, 0xBB,
            0x96, 0x91, 0x98, 0x9F, 0x8A, 0x8D, 0x84, 0x83,
            0xDE, 0xD9, 0xD0, 0xD7, 0xC2, 0xC5, 0xCC, 0xCB,
            0xE6, 0xE1, 0xE8, 0xEF, 0xFA, 0xFD, 0xF4, 0xF3
    };

    private static final AtomicInteger id = new AtomicInteger();



    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024, 2, 2, -2, 0, true),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(new byte[]{0x7e, 0x7f, 0x26, 0x00, 0x00, 0x07, 0x01, 0x38, 0x36, 0x39, 0x31, 0x34, 0x33, 0x30, 0x35, 0x31, 0x39, 0x31, 0x35, 0x37, 0x30,
        0x38, 0x00, 0x34, 0x36, 0x30, 0x30, 0x34, 0x39, 0x33, 0x35, 0x33, 0x38, 0x30, 0x38, 0x31, 0x31, 0x34, 0x00, (byte) 0x94});


        for (int i = 0; i < 1000; i++) {
            int seq = id.compareAndSet(255, 0) ? 255 : id.getAndIncrement();
            String b = Integer.toHexString(seq);
            System.out.println(b);
        }

       /* short len = byteBuf.getShortLE(2);
        int crc_result = 0;
        for (int i = 2; i < len + 1; i++) {
            crc_result = CRC8_TAB[crc_result ^ byteBuf.getByte(i)];
        }
        // 获取校验字段
        int crc8 = byteBuf.getByte(1 + len) & 0xFF;
        System.out.println(crc8 == crc_result);

        System.out.println(toHexString(ByteBufUtil.decodeHexDump(ByteBufUtil.hexDump(byteBuf))));
        System.out.println(byteBuf);

        String test = "7e7f";
        System.out.println(Arrays.toString(ByteBufUtil.decodeHexDump(test)));

        byte pktType = byteBuf.getByte(5);
        System.out.println("pktType = " + pktType);

        System.out.println("byteBuf.readableBytes() = " + byteBuf.readableBytes());*/
//                embeddedChannel.writeInbound(byteBuf);
       /* short s = byteBuf.readShortLE();
        short length = byteBuf.readShortLE();
        byte sn = byteBuf.readByte();
        byte pktType = byteBuf.readByte();
        byte model = byteBuf.readByte();
        String deviceId = byteBuf.readCharSequence(16, Charset.defaultCharset()).toString().trim();
        String imsi = byteBuf.readCharSequence(16, Charset.defaultCharset()).toString().trim();
        byte crc8 = byteBuf.readByte();
        System.out.println("s = " + s + ", hex = " + Integer.toHexString(s));
        System.out.println("length = " + length);
        System.out.println("sn = " + sn);
        System.out.println("pktType = " + pktType);
        System.out.println("model = " + model);
        System.out.println("deviceId = " + new String(deviceId).trim());
        System.out.println("imsi = " + imsi);
        System.out.println("crc8 = " + crc8 + ", hex = " + bytesToHexString(crc8));*/


    }

    public static String bytesToHexString(byte src){
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    public static String toHexString(byte[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(Integer.toHexString(a[i] & 0xFF));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }
}
