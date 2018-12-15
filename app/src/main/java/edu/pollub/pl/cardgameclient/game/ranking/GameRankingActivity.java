package edu.pollub.pl.cardgameclient.game.ranking;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import lombok.RequiredArgsConstructor;
import response.PageResponse;
import response.RankingPositionResponse;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_find_game)
public class GameRankingActivity extends SimpleNetworkActivity {

    private int currentPage = 0;

    private int totalPages = 0;

    private String playerLogin = "";

    private List<RankingPositionResponse> rankingPositions;

    @InjectView(R.id.returnButton)
    private ImageButton returnButton;

    @InjectView(R.id.refreshButton)
    private ImageButton refreshButton;

    @InjectView(R.id.searchView)
    private SearchView rankingSearchView;

    @InjectView(R.id.pageNumber)
    private TextView pageNumberView;

    @InjectView(R.id.page)
    private ListView rankingPositionListView;

    @InjectView(R.id.previousPageButton)
    private ImageButton previousPageButton;

    @InjectView(R.id.nextPageButton)
    private ImageButton nextPageButton;

    @Inject
    private RankingService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnButton.setOnClickListener(v -> comeBack());
        refreshButton.setOnClickListener(v -> simpleNetworkTask(currentPage()).execute());
        nextPageButton.setOnClickListener(v -> simpleNetworkTask(nextPage()).execute());
        previousPageButton.setOnClickListener(v -> simpleNetworkTask(previousPage()).execute());
        rankingSearchView.setOnQueryTextListener(new FindRankingPositionListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        simpleNetworkTask(firstPage()).execute();
    }

    public void refreshRanking(PageResponse<RankingPositionResponse> rankingPositions) {
        this.currentPage = rankingPositions.getCurrentPageNumber();
        this.totalPages = rankingPositions.getTotalPages();
        this.rankingPositions = rankingPositions.getContent();
        List<String> rankingPositionsText = rankingPositions.getContent().stream()
                .map(RankingPositionResponse::toString)
                .collect(Collectors.toList());

        runOnUiThread(() -> refreshRankingView(rankingPositionsText));
    }

    private void refreshRankingView(List<String> gameNames) {
        nextPageButton.setEnabled(currentPage < totalPages-1);
        previousPageButton.setEnabled(currentPage > 0);
        int currentPage = totalPages != 0? this.currentPage+1 : 0;
        pageNumberView.setText(currentPage + "/" + totalPages);
        ArrayAdapter<String> gamesAdapter = new ArrayAdapter<>(this, R.layout.open_game_row, R.id.gameName, gameNames);
        rankingPositionListView.setAdapter(gamesAdapter);
    }

    @RequiredArgsConstructor
    public class GetRankingPositionsTask extends NetworkOperationStrategy {

        private final int requestedPageNumber;
        private final String playerLogin;

        GetRankingPositionsTask() {
            requestedPageNumber = 0;
            playerLogin = "";
        }

        @Override
        public void execute() throws Exception {
            PageResponse<RankingPositionResponse> page = service.getPage(requestedPageNumber, playerLogin);
            refreshRanking(page);
        }
    }

    private GetRankingPositionsTask firstPage() {
        return new GetRankingPositionsTask();
    }

    private GetRankingPositionsTask currentPage() {
        return new GetRankingPositionsTask(currentPage, playerLogin);
    }

    private GetRankingPositionsTask nextPage() {
        return new GetRankingPositionsTask(currentPage+1, playerLogin);
    }

    private GetRankingPositionsTask previousPage() {
        return new GetRankingPositionsTask(currentPage-1, playerLogin);
    }

    public class FindRankingPositionListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String searchTerm) {
            simpleNetworkTask(new GetRankingPositionsTask(currentPage, searchTerm)).execute();
            return false;
        }

    }

}
