package edu.pollub.pl.cardgameclient.game.organization;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.pollub.pl.cardgameclient.communication.http.HttpClient;
import response.GameResponse;
import response.PageResponse;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
public class GameOrganizationService {

    private static final String GAME_ORGANIZATION_URL = SERVER + "/game/organization";
    private static final String GET_GAMES_PAGE_URL = GAME_ORGANIZATION_URL + "/open?size=20&sort=founderLogin";
    private final HttpClient httpClient;

    @Inject
    public GameOrganizationService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public GameResponse organize() throws Exception {
        return httpClient.post(GAME_ORGANIZATION_URL, new TypeReference<GameResponse>() {});
    }

    public void join(String gameId) throws Exception {
        httpClient.put(GAME_ORGANIZATION_URL + "/" + gameId);
    }

    public void close() throws Exception {
        httpClient.delete(GAME_ORGANIZATION_URL);
    }

    public PageResponse<GameResponse> getPage(int pageNumber, String gameName) throws Exception {
        String url = GET_GAMES_PAGE_URL + "&page=" + pageNumber + "&name=" + gameName;
        return httpClient.get(url, new TypeReference<PageResponse<GameResponse>>() {});
    }
}
