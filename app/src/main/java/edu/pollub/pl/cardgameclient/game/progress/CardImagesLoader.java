package edu.pollub.pl.cardgameclient.game.progress;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import lombok.Getter;
import model.Card;

import static android.view.View.INVISIBLE;


class CardImagesLoader {

    @Getter
    private final Context context;

    CardImagesLoader(Context context) {
        this.context = context;
    }

    void hideCard(ImageView cardView) {
        cardView.setVisibility(INVISIBLE);
    }

    void showCard(ImageView cardView, Card card) {
        cardView.setVisibility(View.VISIBLE);
        cardView.setImageResource(this.getCardImg(card));
    }

    private int getCardImg(Card card) {
        return context.getResources().getIdentifier(card.toResourceName(), "drawable", context.getPackageName());
    }
}
