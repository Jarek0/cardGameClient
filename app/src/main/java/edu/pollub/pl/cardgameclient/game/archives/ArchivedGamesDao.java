package edu.pollub.pl.cardgameclient.game.archives;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ArchivedGamesDao {

    @Insert
    void insert(ArchivedGame archivedGame);

    @Query("SELECT * FROM archivedgame WHERE playerLogin = :currentPlayer AND UPPER(enemyLogin) LIKE '%' || UPPER(:searched) || '%' ORDER BY dateTime LIMIT 20 OFFSET (20 * :pageNumber)")
    List<ArchivedGame> getPage(String currentPlayer, String searched, int pageNumber);

    @Query("SELECT * FROM archivedgame WHERE playerLogin = :currentPlayer ORDER BY dateTime LIMIT 20 OFFSET (20 * :pageNumber)")
    List<ArchivedGame> getPage(String currentPlayer,int pageNumber);

    @Query("SELECT COUNT(*) FROM archivedgame WHERE playerLogin = :currentPlayer AND UPPER(enemyLogin) LIKE '%' || UPPER(:login) || '%'")
    int totalElements(String currentPlayer, String login);

    @Query("SELECT COUNT(*) FROM archivedgame WHERE playerLogin = :currentPlayer")
    int totalElements(String currentPlayer);

}
