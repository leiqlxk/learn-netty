package org.lql.netty.chat.protocol;

import com.google.gson.Gson;
import org.lql.netty.chat.message.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Title: Serializer <br>
 * ProjectName: learn-netty <br>
 * description: 用于扩展序列化、反序列化算法 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 8:57 <br>
 */
public interface Serializer {

    // 反序列化方法
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    // 序列化方法
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer{

        // jdk序列化实现
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                ObjectInputStream osi = null;
                try {
                    osi = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) osi.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败");
                }finally {
                    if (osi != null) {
                        try {
                            osi.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                }catch (IOException e) {
                    throw new RuntimeException("序列化失败");
                }
            }
        },

        // json序列化实现
        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
