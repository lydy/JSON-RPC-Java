package tech.lydy.jsonrpc.simple;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import tech.lydy.jsonrpc.IdGenerator;

import java.lang.reflect.Method;
import java.util.Random;

public class SimpleIdGenerator implements IdGenerator {
    private Random random = new Random();
    @Override
    public Object nextId(Method method) {
        return random.nextLong();
    }

    @Override
    public Boolean isIdEquals(Object requestId, Object resultId) {
        if (requestId instanceof Integer) {
            return Integer.valueOf(String.valueOf(resultId)).equals(requestId);
        } else if (requestId instanceof Long) {
            return Long.valueOf(String.valueOf(resultId)).equals(requestId);
        } else {
            return null != requestId && requestId.equals(resultId);
        }
    }
}
