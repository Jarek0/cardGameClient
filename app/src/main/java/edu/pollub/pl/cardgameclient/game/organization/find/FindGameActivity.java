package edu.pollub.pl.cardgameclient.game.organization.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import edu.pollub.pl.cardgameclient.communication.websocket.StompMessageListener;
import edu.pollub.pl.cardgameclient.game.organization.GameOrganizationService;
import edu.pollub.pl.cardgameclient.game.play.PlayGameActivity;
import event.GameStartedEvent;
import lombok.RequiredArgsConstructor;
import response.GameResponse;
import response.PageResponse;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.GAME_QUEUE;
import static java.util.Objects.nonNull;

@ContentView(R.layout.activity_find_game)
public class FindGameActivity extends SimpleNetworkActivity {

    private String gameEndpoint;

    private int currentPage = 0;

    private int totalPages = 0;

    private String gameName = "";

    private List<GameResponse> games;

    private CountDownTimer searchDelayCounter;

    @InjectView(R.id.returnButton)
    private ImageButton returnButton;

    @InjectView(R.id.refreshButton)
    private ImageButton refreshButton;

    @InjectView(R.id.searchView)
    private SearchView gameSearchView;

    @InjectView(R.id.pageNumber)
    private TextView pageNumberView;

    @InjectView(R.id.openGamesList)
    private ListView openGamesListView;

    @InjectView(R.id.previousPageButton)
    private ImageButton previousPageButton;

    @InjectView(R.id.nextPageButton)
    private ImageButton nextPageButton;

    @Inject
    private GameOrganizationService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnButton.setOnClickListener(v -> {
            if(nonNull(gameEndpoint)) unSubscribe(gameEndpoint);
            comeBack();
        });
        refreshButton.setOnClickListener(v -> simpleNetworkTask(currentPage()).execute());
        nextPageButton.setOnClickListener(v -> simpleNetworkTask(nextPage()).execute());
        previousPageButton.setOnClickListener(v -> simpleNetworkTask(previousPage()).execute());
        gameSearchView.setOnQueryTextListener(new FindGameListener());
        openGamesListView.setOnItemClickListener(
                (adapter, v, position, id) -> simpleNetworkTask(new JoinGameTask(games.get(position).getId())).execute()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpleNetworkTask(firstPage()).execute();
    }

    public void refreshGames(PageResponse<GameResponse> games) {
        this.currentPage = games.getCurrentPageNumber();
        this.totalPages = games.getTotalPages();
        this.games = games.getContent();
        List<String> gameNames = games.getContent().stream()
                .map(GameResponse::getFounderLogin)
                .collect(Collectors.toList());

        runOnUiThread(() -> refreshGamesView(gameNames));
    }

    private void refreshGamesView(List<String> gameNames) {
        nextPageButton.setEnabled(currentPage < totalPages-1);
        previousPageButton.setEnabled(currentPage > 0);
        int currentPage = totalPages != 0? this.currentPage+1 : 0;
        pageNumberView.setText(currentPage + "/" + totalPages);
        ArrayAdapter<String> gamesAdapter = new ArrayAdapter<>(this, R.layout.open_game_row, R.id.gameName, gameNames);
        openGamesListView.setAdapter(gamesAdapter);
    }

    @RequiredArgsConstructor
    public class GetOpenGamesTask extends NetworkOperationStrategy {

        private final int requestedPageNumber;
        private final String requestedGameName;

        GetOpenGamesTask() {
            requestedPageNumber = 0;
            requestedGameName = "";
        }

        @Override
        public void execute() throws Exception {
            PageResponse<GameResponse> page = service.getPage(requestedPageNumber, requestedGameName);
            refreshGames(page);
        }

    }

    private GetOpenGamesTask firstPage() {
        return new GetOpenGamesTask();
    }

    private GetOpenGamesTask currentPage() {
        return new GetOpenGamesTask(currentPage, gameName);
    }

    private GetOpenGamesTask nextPage() {
        return new GetOpenGamesTask(currentPage+1, gameName);
    }

    private GetOpenGamesTask previousPage() {
        return new GetOpenGamesTask(currentPage-1, gameName);
    }

    private void listenForGameStarted(String gameId) {
        gameEndpoint = GAME_QUEUE + "/" + gameId;
        subscribe(gameEndpoint, new GameStartedListener());
    }

    @RequiredArgsConstructor
    public class JoinGameTask extends NetworkOperationStrategy {

        private final String gameId;

        @Override
        public void execute() throws Exception {
            listenForGameStarted(gameId);
            service.join(gameId);
        }

    }

    public class FindGameListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String searchTerm) {
            simpleNetworkTask(new GetOpenGamesTask(currentPage, searchTerm)).execute();
            return false;
        }

    }

    public class GameStartedListener extends StompMessageListener<GameStartedEvent> {

        @Override
        public void onMessage(GameStartedEvent event)  {
            unSubscribe(gameEndpoint);
            showToast(R.string.gameStarted);
            Intent intent = new Intent(FindGameActivity.this, PlayGameActivity.class);
            intent.putExtra("event", event);
            startActivityForResult(intent, 0);
        }

        GameStartedListener() {
            super(GameStartedEvent.class);
        }
    }
}