package tech.lydy.jsonrpc.simple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import tech.lydy.jsonrpc.Serializer;

import java.lang.reflect.Method;
import java.util.List;

public class FastjsonSerializer extends Serializer {

    @Override
    public <T> T deserialize(String data, Class<? extends T> clzz) {
        if (String.class.isAssignableFrom(clzz) || Number.class.isAssignableFrom(clzz)) {
            return (T) data;
        } else {
            return JSON.parseObject(data, clzz);
        }
    }

    @Override
    public <T> List<T> deserializeList(String data, Class<? extends T> clzz) {
        return (List<T>) JSONArray.parseArray(data, clzz);
    }

    @Override
    public <T> T deserializeByMethod(String data, Method method) {
        if (!getMethodSerializers().containsKey(method.getName())) {
            return null;
        }
        return (T) getMethodSerializers().get(method.getName()).apply(data);
    }

    @Override
    public <T> List<T> deserializeListByMethod(String data, Method method) {
        if (!getMethodSerializers().containsKey(method.getName())) {
            return null;
        }
        return (List<T>) getMethodSerializers().get(method.getName()).apply(data);
    }

    @Override
    public String serialize(Object t) {
        return JSON.toJSONString(t);
    }

}
