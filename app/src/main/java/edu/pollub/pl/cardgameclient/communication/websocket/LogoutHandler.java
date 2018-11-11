package edu.pollub.pl.cardgameclient.communication.websocket;


import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class LogoutHandler {

    @Getter
    private boolean loggedOut;

    public Runnable onLogout;

    public LogoutHandler(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void deactivate() {
        loggedOut = true;
    }
}
