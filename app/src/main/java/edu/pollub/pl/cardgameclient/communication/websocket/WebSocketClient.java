package edu.pollub.pl.cardgameclient.communication.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.pollub.pl.cardgameclient.communication.http.HttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static java.util.Objects.nonNull;

@Singleton
public class WebSocketClient extends WebSocketListener {

    private Map<String, QueueHandler> queues = new HashMap<>();
    private CloseHandler closeHandler;
    private String id = "sub-001";
    private WebSocket webSocket;

    private final HttpClient httpClient;

    @Inject
    public WebSocketClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public QueueHandler subscribe(String queue) {
        QueueHandler handler = new QueueHandler(queue);
        queues.put(queue, handler);
        if(nonNull(webSocket)){
            sendSubscribeMessage(webSocket, queue);
        }
        return handler;
    }

    public void unSubscribe(String queue) {
        queues.remove(queue);
    }

    public Optional<QueueHandler> getQueueHandler(String queue) {
        if(queues.containsKey(queue)){
            return Optional.of(queues.get(queue));
        }
        return Optional.empty();
    }

    public void connect(String address) {
        OkHttpClient client = httpClient.getClient();

        Request request = new Request.Builder()
                .url(address)
                .build();

        client.newWebSocket(request, this);

        client.dispatcher().executorService().shutdown();
    }

    @Override public void onOpen(WebSocket webSocket, Response response) {

        this.webSocket = webSocket;

        sendConnectMessage(webSocket);

        for(String queue : queues.keySet()){
            sendSubscribeMessage(webSocket, queue);
        }


        closeHandler = new CloseHandler(webSocket);
    }

    private void sendConnectMessage(WebSocket webSocket) {
        StompMessage message = new StompMessage("CONNECT");
        message.put("accept-version", "1.1");
        message.put("heart-beat", "10000,10000");
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendSubscribeMessage(WebSocket webSocket, String queue) {
        StompMessage message = new StompMessage("SUBSCRIBE");
        message.put("id", id);
        message.put("destination", queue);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendMessage(String jsonMessage, String queue) {
        if(nonNull(webSocket)) {
            StompMessage message = new StompMessage(jsonMessage);
            message.put("id", id);
            message.put("destination", queue);
            webSocket.send(StompMessageSerializer.serialize(message));
        }
    }

    public void disconnect() {
        if(nonNull(webSocket)){
            closeHandler.close();
            webSocket = null;
            closeHandler = null;
        }
    }

    public boolean isConnected() {
        return nonNull(closeHandler);
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        StompMessage message = StompMessageSerializer.deserialize(text);
        String queue = message.getHeader("destination");
        if(queues.containsKey(queue)){
            queues.get(queue).onMessage(message);
        }
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

}
