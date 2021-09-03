package org.lql.netty.chatandrpc.protocol;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Title: SerializeEnum <br>
 * ProjectName: coldchain <br>
 * description: 设备及序列类型映射 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/9/1 10:32 <br>
 */
public enum SerializeEnum {

    // 冷链长链类型终端
    DEVICE_MODEL_CC_XD_001(11, 0),
    // 冷链短链类型终端
    DEVICE_MODEL_CC_XD_002(12, 0);

    private int code;

    private int serializeSn;

    SerializeEnum(int code, int serializeSn) {
        this.code = code;
        this.serializeSn = serializeSn;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSerializeSn() {
        return serializeSn;
    }

    public void setSerializeSn(int serializeSn) {
        this.serializeSn = serializeSn;
    }

    /*public SerializeEnum getSerializeEnumByCode(int code) {
        SerializeEnum[] values = SerializeEnum.values();
        List<SerializeEnum> list = Arrays.stream(values).filter(item -> item.getCode() == code).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }else {
            return list.get(0);
        }
    }*/
}
