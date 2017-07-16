package tech.lydy.jsonrpc;

import java.lang.reflect.Method;
import java.util.Collection;

public interface Serializer {
    <T> T deserialize(String data, Class<? extends T> clzz);

    <T> Collection<T> deserializeList(String data, Class<? extends T> clzz);

    <T> T deserializeByMethod(String data, Method method);

    <T> Collection<T> deserializeListByMethod(String data, Method method);

    String serialize(Object t);
}
