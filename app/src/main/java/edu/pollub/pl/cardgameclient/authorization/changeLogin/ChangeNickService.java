package edu.pollub.pl.cardgameclient.authorization.changeLogin;

import javax.inject.Inject;
import javax.inject.Singleton;

import command.ChangeLoginCommand;
import edu.pollub.pl.cardgameclient.communication.http.HttpClient;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class ChangeNickService {

    private static final String CHANGE_NICK_URL = SERVER + "/authentication/registration/login";
    private final HttpClient httpClient;

    @Inject
    public ChangeNickService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void changeNick(ChangeLoginCommand command)  throws Exception {
        httpClient.put(CHANGE_NICK_URL, command);
    }

}
