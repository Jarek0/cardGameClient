package edu.pollub.pl.cardgameclient.game.progress;

import model.Card;

public interface CardMoveCallback {

    void useCard(Card card);

    void stop();

    boolean canCardBeUsed(Card card);

    boolean canStop();
}
