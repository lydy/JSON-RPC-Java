package tech.lydy.jsonrpc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;

public class JsonRpcProxy implements MethodInterceptor {

    private Executor rpcExecutor;

    private Serializer serializer;

    private IdGenerator idGenerator;

    public JsonRpcProxy(Executor rpcExecutor, Serializer serializer, IdGenerator idGenerator) {
        this.rpcExecutor = rpcExecutor;
        this.serializer = serializer;
        this.idGenerator = idGenerator;
    }

    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //Object's methods
        if (Object.class.equals(method.getDeclaringClass())) {
            return methodProxy.invokeSuper(o, args);
        }

        JsonRpcRequest rpcRequest = new JsonRpcRequest();
        rpcRequest.setId(idGenerator.nextId(method));
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation:annotations) {
            if (annotation instanceof MethodName) {
                rpcRequest.setMethod(((MethodName) annotation).name());
            }
        }
        if (null == rpcRequest.getMethod()) {
            rpcRequest.setMethod(method.getName());
        }

        rpcRequest.setParams(args);
        Integer idx = 0;
        for (Annotation[] anns: method.getParameterAnnotations()) {
            idx++;
            for (Annotation annotation: anns) {
                if (annotation instanceof ParamType && ((ParamType) annotation).value() == ParamType.Type.Single) {
                    rpcRequest.setParams(args[idx]);
                    break;
                }
            }
            if (rpcRequest.getParams() != null) {
                break;
            }
        }

        JsonRpcResult result = rpcExecutor.execute(rpcRequest);
        if (result.getError() != null) {
            throw new Error(result.getError());
        } else if (!idGenerator.isIdEquals(rpcRequest.getId(), result.getId())) {
            throw new ResultIdNotEqualsException("json rpc id not match, requestId:" + rpcRequest.getId() + ",resultId:" + result.getId());
        }

        Class<?> returnType = method.getReturnType();
        if (returnType.getSuperclass() == null || serializer.getMethodSerializers().containsKey(method.getName())) {
            return serializer.deserializeByMethod(result.getResult(), method);
        } else if (void.class.isAssignableFrom(returnType)) {
            return Void.TYPE;
        } else if (Collection.class.isAssignableFrom(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            Type firstArgType = parameterizedType.getActualTypeArguments()[0];
            Boolean isClass = firstArgType instanceof Class;
            if (serializer.getMethodSerializers().containsKey(method.getName()) ||
                    firstArgType instanceof TypeVariable ||
                    (isClass && ((Class)firstArgType).getSuperclass() == null)) {
                return serializer.deserializeListByMethod(result.getResult(), method);
            } else if (isClass) {
                return serializer.deserializeList(result.getResult(), (Class<?>) firstArgType);
            } else {
                throw new RuntimeException("not implement it yet");
            }
        } else {
            return serializer.deserialize(result.getResult(), returnType);
        }

    }

    public <T> T newInstance(Class<T> clzz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clzz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }
}
