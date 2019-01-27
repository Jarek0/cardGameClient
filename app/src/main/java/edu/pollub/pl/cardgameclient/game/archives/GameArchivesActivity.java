package edu.pollub.pl.cardgameclient.game.archives;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.activity.SharedPreferencesActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.LOGIN_KEY;

@ContentView(R.layout.activity_find_game)
public class GameArchivesActivity extends SharedPreferencesActivity {

    private int currentPage = 0;

    private int totalPages = 0;

    private String playerLogin = "";

    private List<ArchivedGame> archivedGames;

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
    private ArchivedGameService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnButton.setOnClickListener(v -> comeBack());
        refreshButton.setOnClickListener(v -> getPage(currentPage));
        nextPageButton.setOnClickListener(v -> getPage(nextPage()));
        previousPageButton.setOnClickListener(v -> getPage(previousPage()));
        rankingSearchView.setOnQueryTextListener(new FindArchivedGamesPageListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        getPage(currentPage);
    }

    private int nextPage() {
        return currentPage + 1;
    }

    private int previousPage() {
        return currentPage - 1;
    }

    private void getPage(int pageNumber) {
        Context c = getApplicationContext();
        if(pageNumber < 0) return;
        totalPages = service.totalPages(fetchString(LOGIN_KEY), playerLogin, c);
        if(totalPages == 0) {
            refreshView(Collections.emptyList());
        }
        else if(pageNumber < totalPages) {
            currentPage = pageNumber;
            archivedGames = service.getPage(fetchString(LOGIN_KEY), playerLogin, pageNumber, c);
            List<String> archivedGamesText = archivedGames.stream()
                    .map(ArchivedGame::toString)
                    .collect(Collectors.toList());
            refreshView(archivedGamesText);
        }
    }

    private void refreshView(List<String> gameNames) {
        nextPageButton.setEnabled(currentPage < totalPages-1);
        previousPageButton.setEnabled(currentPage > 0);
        int currentPage = totalPages != 0? this.currentPage+1 : 0;
        pageNumberView.setText(currentPage + "/" + totalPages);
        ArrayAdapter<String> archivedGamesAdapter = new ArrayAdapter<>(this, R.layout.archived_game, R.id.archivedGame, gameNames);
        rankingPositionListView.setAdapter(archivedGamesAdapter);
    }

    public class FindArchivedGamesPageListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String searchTerm) {
            playerLogin = searchTerm;
            getPage(currentPage);
            return false;
        }

    }
}
