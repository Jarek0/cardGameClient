package edu.pollub.pl.cardgameclient.game.progress;

import android.widget.TextView;

class EnemyCards {

    private final static int INITIAL_ENEMY_CARDS_COUNT = 5;

    private final TextView enemyCardsTextView;

    private int enemyCardsCount;

    EnemyCards(TextView cardsStackTextView) {
        this.enemyCardsTextView = cardsStackTextView;
        actualizeCardsCount(INITIAL_ENEMY_CARDS_COUNT);
    }

    void actualizeCardsCount(int enemyCardsCount) {
        this.enemyCardsCount = enemyCardsCount;
        enemyCardsTextView.setText("Enemy cards: " + enemyCardsCount);
    }
}
