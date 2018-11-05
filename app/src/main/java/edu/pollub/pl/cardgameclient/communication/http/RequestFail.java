package edu.pollub.pl.cardgameclient.communication.http;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestFail extends Exception {

    private String messageId;

    public RequestFail(String msg) {
        super(msg);
        this.messageId = msg;
    }

}
