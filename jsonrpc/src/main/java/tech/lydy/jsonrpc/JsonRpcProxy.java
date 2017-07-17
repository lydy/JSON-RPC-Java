package tech.lydy.jsonrpc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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
        if (args.length == 1) {
            if (method.getParameterAnnotations()[0].length > 0) {
                for (Annotation annotation : method.getParameterAnnotations()[0]) {
                    if (annotation instanceof ParamType && ((ParamType) annotation).value() == ParamType.Type.Single) {
                        rpcRequest.setParams(args[0]);
                    }
                }
            }
        }

        JsonRpcResult result = rpcExecutor.execute(rpcRequest);
        if (result.getError() != null) {
            throw new Error(result.getError());
        } else if (!idGenerator.isIdEquals(rpcRequest.getId(), result.getId())) {
            throw new ResultIdNotEqualsException("json rpc id not match, requestId:" + rpcRequest.getId() + ",resultId:" + result.getId());
        }

        Class<?> returnType = method.getReturnType();
        if (void.class.isAssignableFrom(returnType)) {
            return Void.TYPE;
        } else if (Collection.class.isAssignableFrom(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            if (parameterizedType.getActualTypeArguments()[0] instanceof TypeVariable) {
                return serializer.deserializeListByMethod(result.getResult(), method);
            }
            return serializer.deserializeList(result.getResult(), (Class<?>) parameterizedType.getActualTypeArguments()[0]);
        } else if (method.getGenericReturnType() instanceof TypeVariable) {
            return serializer.deserializeByMethod(result.getResult(), method);
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
