package edu.pollub.pl.cardgameclient.game.play;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import lombok.Getter;
import model.Card;

import static android.view.View.INVISIBLE;


public class CardImagesLoader {

    @Getter
    private final Context context;

    CardImagesLoader(Context context) {
        this.context = context;
    }

    public void hideCard(ImageView cardView) {
        cardView.setVisibility(INVISIBLE);
    }

    public void showCard(ImageView cardView, Card card) {
        cardView.setVisibility(View.VISIBLE);
        cardView.setImageResource(this.getCardImg(card));
    }

    private int getCardImg(Card card) {
        return context.getResources().getIdentifier(card.toResourceName(), "drawable", context.getPackageName());
    }
}
