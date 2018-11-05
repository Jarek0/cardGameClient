package edu.pollub.pl.cardgameclient.authorization.changePassword;

import javax.inject.Inject;
import javax.inject.Singleton;

import command.ChangePasswordCommand;
import edu.pollub.pl.cardgameclient.communication.http.HttpClient;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class ChangePasswordService {

    private static final String CHANGE_PASSWORD_URL = SERVER + "/authentication/password";
    private final HttpClient httpClient;

    @Inject
    public ChangePasswordService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void changePassword(ChangePasswordCommand command) throws Exception {
        httpClient.put(CHANGE_PASSWORD_URL, command);
    }
}
