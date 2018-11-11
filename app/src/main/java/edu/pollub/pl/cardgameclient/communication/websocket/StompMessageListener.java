package edu.pollub.pl.cardgameclient.communication.websocket;

import event.CardGameEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class StompMessageListener<T extends CardGameEvent> {

    private final Class<T> c;

    public abstract void onMessage(T messageBody);

    boolean supports(CardGameEvent event) {
        return this.c.isInstance(event);
    }

}
