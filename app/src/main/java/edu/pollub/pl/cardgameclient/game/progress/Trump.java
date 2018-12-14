package edu.pollub.pl.cardgameclient.game.progress;

import android.widget.ImageView;

import model.Card;
import model.CardColor;


class Trump {

    private final CardImagesLoader cardImagesLoader;

    private final Card trump;

    private final ImageView trumpImageView;

    Trump(CardImagesLoader cardImagesLoader, ImageView trumpImageView, Card trump) {
        this.cardImagesLoader = cardImagesLoader;
        this.trump = trump;
        this.trumpImageView = trumpImageView;
        cardImagesLoader.showCard(trumpImageView, trump);
    }

    CardColor getColor() {
        return trump.getColor();
    }
}
