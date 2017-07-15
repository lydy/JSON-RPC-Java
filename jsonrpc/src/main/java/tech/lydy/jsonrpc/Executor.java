package tech.lydy.jsonrpc;

public interface Executor {
    JsonRpcResult execute(JsonRpcRequest params) throws Throwable;
}
