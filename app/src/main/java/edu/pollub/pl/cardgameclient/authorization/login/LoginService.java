package edu.pollub.pl.cardgameclient.authorization.login;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import command.LoginCommand;
import edu.pollub.pl.cardgameclient.communication.http.HttpClient;
import response.AuthenticatedResponse;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class LoginService {

    private static final String LOGIN_URL = SERVER + "/authentication";
    private final HttpClient httpClient;

    @Inject
    public LoginService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public AuthenticatedResponse login(LoginCommand command) throws Exception {
        return httpClient.post(LOGIN_URL, command, new TypeReference<AuthenticatedResponse>() {});
    }

    public void logout() throws Exception {
        httpClient.delete(LOGIN_URL);
    }
}
