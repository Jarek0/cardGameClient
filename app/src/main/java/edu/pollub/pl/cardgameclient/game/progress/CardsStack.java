package edu.pollub.pl.cardgameclient.game.progress;

import android.widget.TextView;

class CardsStack {

    private final static int INITIAL_STACK_CARDS_COUNT = 26;

    private final TextView cardsStackTextView;

    private int stackCardsCount;

    CardsStack(TextView cardsStackTextView) {
        this.cardsStackTextView = cardsStackTextView;
        actualizeCardsCount(INITIAL_STACK_CARDS_COUNT);
    }

    void actualizeCardsCount(int stackCardsCount) {
        this.stackCardsCount = stackCardsCount;
        cardsStackTextView.setText("Cards on stack: " + stackCardsCount);
    }
}
