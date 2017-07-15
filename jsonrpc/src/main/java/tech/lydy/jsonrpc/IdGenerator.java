package tech.lydy.jsonrpc;

import java.lang.reflect.Method;

//当需要保存id时，需要实现该generator，通过该generator实现，如可以通过4个步骤实现：生成id -> 锁定 -> 取id -> 解锁
public interface IdGenerator {
    Object nextId(Method method);
    Boolean isIdEquals(Object requestId, Object resultId);
}
