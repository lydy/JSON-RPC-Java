package tech.lydy.jsonrpc.simple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import tech.lydy.jsonrpc.Serializer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FastjsonSerializer implements Serializer {

    private Map<String, Function<String, Object>> methodSerializers = new HashMap<>();

    public void addMethodSerializer(String methodName, Function fi) {
        methodSerializers.put(methodName, fi);
    }

    public Map<String, Function<String, Object>> getMethodSerializers() {
        return methodSerializers;
    }

    public void setMethodSerializers(Map<String, Function<String, Object>> methodSerializers) {
        this.methodSerializers = methodSerializers;
    }

    @Override
    public <T> T deserialize(String data, Class<? extends T> clzz) {
        return JSON.parseObject(data, clzz);
    }

    @Override
    public <T> List<T> deserializeList(String data, Class<? extends T> clzz) {
        return (List<T>) JSONArray.parseArray(data, clzz);
    }

    @Override
    public <T> T deserializeByMethod(String data, Method method) {
        if (!this.methodSerializers.containsKey(method.getName())) {
            return null;
        }
        return (T) this.methodSerializers.get(method.getName()).apply(data);
    }

    @Override
    public <T> List<T> deserializeListByMethod(String data, Method method) {
        if (!this.methodSerializers.containsKey(method.getName())) {
            return null;
        }
        return (List<T>) this.methodSerializers.get(method.getName()).apply(data);
    }

    @Override
    public String serialize(Object t) {
        return JSON.toJSONString(t);
    }

}
