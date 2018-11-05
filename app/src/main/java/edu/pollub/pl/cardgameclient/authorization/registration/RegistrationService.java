package edu.pollub.pl.cardgameclient.authorization.registration;

import javax.inject.Inject;
import javax.inject.Singleton;

import command.RegisterCommand;
import edu.pollub.pl.cardgameclient.communication.http.HttpClient;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class RegistrationService {

    private static final String REGISTER_URL = SERVER + "/authentication/registration";
    private final HttpClient httpClient;

    @Inject
    public RegistrationService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    void register(RegisterCommand command) throws Exception {
        httpClient.post(REGISTER_URL, command);
    }

}
