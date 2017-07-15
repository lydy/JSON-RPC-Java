package tech.lydy.jsonrpc.simple;

import tech.lydy.jsonrpc.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleExecutor implements Executor {

    private URL serviceUrl;
    private Serializer serializer;

    public SimpleExecutor(URL serviceUrl, Serializer serializer) {
        this.serializer = serializer;
        this.serviceUrl = serviceUrl;
    }

    public JsonRpcResult execute(JsonRpcRequest req) throws IOException, InvalidResCodeException {
        String reqStr = serializer.serialize(req);
        JsonRpcResult res = null;
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("POST");
        OutputStream outputStream = connection.getOutputStream();
        byte[] requestStringBytes = reqStr.getBytes();
        outputStream.write(requestStringBytes);
        outputStream.close();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            StringBuffer sb = new StringBuffer();
            String readLine;
            BufferedReader responseReader;
            responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            res = serializer.deserialize(sb.toString(), JsonRpcResult.class);
        } else {
            throw new InvalidResCodeException("invalid response code:" + responseCode);
        }

        return res;
    }
}
