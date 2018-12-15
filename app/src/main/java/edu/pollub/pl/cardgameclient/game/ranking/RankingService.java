package edu.pollub.pl.cardgameclient.game.ranking;

import com.fasterxml.jackson.core.type.TypeReference;


import javax.inject.Inject;
import javax.inject.Singleton;

import edu.pollub.pl.cardgameclient.communication.http.HttpClient;
import response.PageResponse;
import response.RankingPositionResponse;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.SERVER;

@Singleton
class RankingService {

    private static final String RANKING_URL = SERVER + "/ranking?size=20&sort=points,desc";
    private final HttpClient httpClient;

    @Inject
    public RankingService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    PageResponse<RankingPositionResponse> getPage(int pageNumber, String playerLogin) throws Exception {
        String url = RANKING_URL + "&page=" + pageNumber + "&playerLogin=" + playerLogin;
        return httpClient.get(url, new TypeReference<PageResponse<RankingPositionResponse>>() {});
    }
}
