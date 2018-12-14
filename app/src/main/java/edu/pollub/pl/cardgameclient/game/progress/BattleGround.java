package edu.pollub.pl.cardgameclient.game.progress;

import android.widget.ImageView;

import model.Card;

class BattleGround {

    private final CardImagesLoader cardImagesLoader;

    private final ImageView playerCard;

    private final ImageView enemyCard;

    BattleGround(CardImagesLoader cardImagesLoader, ImageView playerCard, ImageView enemyCard) {
        this.cardImagesLoader = cardImagesLoader;
        this.playerCard = playerCard;
        this.enemyCard = enemyCard;
        this.clear();
    }

    void putPlayerCard(Card card) {
        cardImagesLoader.showCard(playerCard, card);
    }

    void clear() {
        cardImagesLoader.hideCard(playerCard);
        cardImagesLoader.hideCard(enemyCard);
    }

    void putEnemyCard(Card card) {
        cardImagesLoader.showCard(enemyCard, card);
    }
}
