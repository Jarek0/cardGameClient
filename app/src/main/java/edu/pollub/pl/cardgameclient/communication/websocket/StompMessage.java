package edu.pollub.pl.cardgameclient.communication.websocket;

import java.util.HashMap;
import java.util.Map;

import event.CardGameEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StompMessage {
    private Map<String, String> headers = new HashMap<>();
    private CardGameEvent body;
    private String command;

    public StompMessage(String command) {
        this.command = command;
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public void put(String name, String value){
        headers.put(name, value);
    }

    public void setContent(CardGameEvent body){
        this.body = body;
    }

    public CardGameEvent getContent() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCommand() {
        return command;
    }
}