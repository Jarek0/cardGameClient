package edu.pollub.pl.cardgameclient.communication.websocket;

import java.util.HashSet;
import java.util.Set;

public class QueueHandler {

    private String queue;
    private Set<StompMessageListener> listeners = new HashSet<>();
    QueueHandler(String queue) {
        this.queue = queue;
    }

    public String getQueue() {
        return queue;
    }

    public void addListener(StompMessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(StompMessageListener listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void onMessage(StompMessage message) {
        for(StompMessageListener listener : listeners){
            if(listener.supports(message.getContent())) {
                listener.onMessage(message.getContent());
            }
        }

    }

}
