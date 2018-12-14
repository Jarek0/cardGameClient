package edu.pollub.pl.cardgameclient.communication.websocket;

import event.CardGameEvent;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class StompMessageListener<T extends CardGameEvent> {

    private final Class<T> c;

    public abstract void onMessage(T event);

    boolean supports(Class<T> eventClass) {
        return this.c.equals(eventClass);
    }

}
