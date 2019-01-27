package edu.pollub.pl.cardgameclient.game.archives;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Data;

@Entity
@Data
public class ArchivedGame {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String dateTime;

    private String playerLogin;

    private String enemyLogin;

    private int points;

    private boolean won;

    ArchivedGame(String playerLogin, String enemyLogin, int points, boolean won) {
        this.playerLogin = playerLogin;
        this.enemyLogin = enemyLogin;
        this.points = points;
        this.won = won;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    public String toString() {
        return (won ? "Win, " : "Defeat, ") + this.enemyLogin + ", " + points + "\n" + dateTime;
    }

}
