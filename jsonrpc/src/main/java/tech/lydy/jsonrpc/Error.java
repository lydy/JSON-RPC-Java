package tech.lydy.jsonrpc;

/**
 * Date: 2017-07-15 19:37
 * Author: yuhongyu
 */

public class Error extends Exception {

    private JsonRpcResult.ErrorEntity errorEntity;

    public Error(JsonRpcResult.ErrorEntity errorEntity) {
        super(errorEntity.getMessage());
        this.errorEntity = errorEntity;
    }

    public JsonRpcResult.ErrorEntity getErrorEntity() {
        return errorEntity;
    }

    public void setErrorEntity(JsonRpcResult.ErrorEntity errorEntity) {
        this.errorEntity = errorEntity;
    }

}
