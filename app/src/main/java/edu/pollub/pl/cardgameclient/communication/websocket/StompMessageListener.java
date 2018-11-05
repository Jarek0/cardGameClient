package edu.pollub.pl.cardgameclient.communication.websocket;

public interface StompMessageListener {
    void onMessage(StompMessage message);
}
