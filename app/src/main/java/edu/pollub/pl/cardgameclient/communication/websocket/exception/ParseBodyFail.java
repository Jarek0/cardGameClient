package edu.pollub.pl.cardgameclient.communication.websocket.exception;

public class ParseBodyFail extends RuntimeException {

    public ParseBodyFail(Exception e) {
        super(e);
    }

    public ParseBodyFail(String body, Exception e) {
        super("Error while parse object: " + body, e);
    }
}