package edu.pollub.pl.cardgameclient.game.archives;

import android.content.Context;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public class ArchivedGameService {

    public void archiveGame(int points, String playerLogin, String enemyLogin, boolean won, Context context) {
        ArchivedGamesDatabase.getDatabase(context)
                .daoAccess()
                .insert(new ArchivedGame(playerLogin, enemyLogin, points, won));
    }

    List<ArchivedGame> getPage(String currentPlayer, String searchedPlayer, int pageNumber, Context context) {
        ArchivedGamesDao dao = ArchivedGamesDatabase.getDatabase(context)
                .daoAccess();
        return searchedPlayer.isEmpty() ? dao.getPage(currentPlayer, pageNumber) : dao.getPage(currentPlayer, searchedPlayer, pageNumber);
    }

    int totalPages(String currentPlayer, String searchedPlayer, Context context) {
        ArchivedGamesDao dao = ArchivedGamesDatabase.getDatabase(context)
                .daoAccess();
        int totalElements = searchedPlayer.isEmpty() ? dao.totalElements(currentPlayer) : dao.totalElements(currentPlayer, searchedPlayer);
        return (int)Math.ceil(((double)totalElements)/20.0);
    }
}
