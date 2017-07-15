package tech.lydy.jsonrpc;

public class JsonRpcFactory {

    private Executor rpcExecutor;

    private Serializer serializer;

    private IdGenerator idGenerator;

    public Executor getRpcExecutor() {
        return rpcExecutor;
    }

    public void setRpcExecutor(Executor rpcExecutor) {
        this.rpcExecutor = rpcExecutor;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public <T> T createClient(Class<T> clzz) {
        JsonRpcProxy proxy = new JsonRpcProxy(rpcExecutor, serializer, idGenerator);
        return proxy.newInstance(clzz);
    };

}
