package tech.lydy.jsonrpc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class Serializer {
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

    public abstract <T> T deserialize(String data, Class<? extends T> clzz);

    public abstract <T> Collection<T> deserializeList(String data, Class<? extends T> clzz);

    public abstract <T> T deserializeByMethod(String data, Method method);

    public abstract <T> Collection<T> deserializeListByMethod(String data, Method method);

    public abstract String serialize(Object t);
}
