package edu.pollub.pl.cardgameclient.communication.websocket;

import org.roboguice.shaded.goole.common.collect.Sets;

import java.util.Set;

import event.CardGameEvent;

public class QueueHandler {

    private String queue;
    private Set<StompMessageListener> listeners = Sets.newConcurrentHashSet();
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

    public <T extends CardGameEvent> void removeListeners(Class<T> eventClass) {
        listeners.removeIf(l -> l.supports(eventClass));
    }

    @SuppressWarnings("unchecked")
    void onMessage(StompMessage message) {
        for(StompMessageListener listener : listeners){
            if(listener.supports(message.getContent().getClass())) {
                listener.onMessage(message.getContent());
            }
        }

    }

}
