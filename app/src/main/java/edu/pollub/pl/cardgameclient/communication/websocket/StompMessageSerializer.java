package edu.pollub.pl.cardgameclient.communication.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;

import edu.pollub.pl.cardgameclient.communication.websocket.exception.ParseBodyFail;
import event.CardGameEvent;

import static java.util.Objects.isNull;

@Singleton
class StompMessageSerializer {

    private final ObjectMapper mapper = new ObjectMapper();

    public String serialize(StompMessage message) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(message.getCommand()).append("\n");

        for(Map.Entry<String, String> header : message.getHeaders().entrySet())
        {
            buffer.append(header.getKey()).append(":").append(header.getValue()).append("\n");
        }

        buffer.append("\n");
        buffer.append(serializeBody(message.getContent()));
        buffer.append('\0');

        return buffer.toString();
    }

    private String serializeBody(CardGameEvent body) {
        try {
            if(isNull(body)) return "";
            return mapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new ParseBodyFail(e);
        }
    }

    public StompMessage deserialize(String message) {
        String[] lines = message.split("\n");

        String command = lines[0].trim();

        StompMessage result = new StompMessage(command);

        int i=1;
        for(; i < lines.length; ++i){
            String line = lines[i].trim();
            if(line.equals("")){
                break;
            }
            String[] parts = line.split(":");
            String name = parts[0].trim();
            String value = "";
            if(parts.length==2){
                value = parts[1].trim();
            }
            result.put(name, value);
        }

        StringBuilder sb = new StringBuilder();

        for(; i < lines.length; ++i){
            sb.append(lines[i]);
        }

        String body = sb.toString().trim();

        result.setContent(parseBody(body));
        return result;
    }

    private CardGameEvent parseBody(String body) {
        try {
            if(body.isEmpty()) return null;
            return mapper.readValue(body, CardGameEvent.class);
        } catch (IOException e) {
            throw new ParseBodyFail(body, e);
        }
    }


}
