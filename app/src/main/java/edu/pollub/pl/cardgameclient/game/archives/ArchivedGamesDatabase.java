package edu.pollub.pl.cardgameclient.game.archives;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ArchivedGame.class}, version = 3, exportSchema = false)
abstract class ArchivedGamesDatabase extends RoomDatabase {

    abstract ArchivedGamesDao daoAccess();

    private static volatile ArchivedGamesDatabase INSTANCE;

    static ArchivedGamesDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ArchivedGamesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ArchivedGamesDatabase.class, "archived_games_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
