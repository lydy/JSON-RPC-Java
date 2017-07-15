package tech.lydy.jsonrpc;

import java.util.Collection;

public interface Serializer {
    <T> T deserialize(String data, Class<? extends T> clzz);

    <T> Collection<T> deserializeList(String data, Class<? extends T> clzz);

    String serialize(Object t);
}
