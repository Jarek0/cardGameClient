package edu.pollub.pl.cardgameclient.game.progress.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.pollub.pl.cardgameclient.communication.http.HttpClient;
import model.Card;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class GameProgressService {

    private static final String GAME_PROGRESS_URL = SERVER + "/game/battle";
    private static final String GAME_ATTACK_URL = GAME_PROGRESS_URL + "/attack";
    private static final String GAME_DEFENSE_URL = GAME_PROGRESS_URL + "/defense";
    private final HttpClient httpClient;

    @Inject
    public GameProgressService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void attack(String gameId, Card attackCard) throws Exception {
        httpClient.post(GAME_ATTACK_URL + "/" + gameId, attackCard);
    }

    public void stopAttack(String gameId) throws Exception {
        httpClient.delete(GAME_ATTACK_URL + "/" + gameId);
    }

    public void defense(String gameId, Card defenseCard) throws Exception {
        httpClient.post(GAME_DEFENSE_URL + "/" + gameId, defenseCard);
    }

    public void stopDefense(String gameId) throws Exception {
        httpClient.delete(GAME_DEFENSE_URL + "/" + gameId);
    }
}
