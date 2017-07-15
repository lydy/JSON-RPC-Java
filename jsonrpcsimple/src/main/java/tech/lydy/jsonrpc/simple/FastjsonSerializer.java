package tech.lydy.jsonrpc.simple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import tech.lydy.jsonrpc.Serializer;

import java.util.List;

public class FastjsonSerializer implements Serializer {

    public <T> T deserialize(String data, Class<? extends T> clzz) {
        return JSON.parseObject(data, clzz);
    }

    public <T> List<T> deserializeList(String data, Class<? extends T> clzz) {
        return (List<T>) JSONArray.parseArray(data, clzz);
    }

    public String serialize(Object t) {
        return JSON.toJSONString(t);
    }

}
