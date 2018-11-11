package edu.pollub.pl.cardgameclient.game.play;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import event.GameStartedEvent;
import model.Card;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_game)
public class PlayGameActivity extends SimpleNetworkActivity {

    private String gameId;

    private List<Card> cards;

    private Card trump;

    @InjectView(R.id.playerCards)
    private TwoWayView playerCardsListView;

    @InjectView(R.id.returnButton)
    private ImageButton returnButton;

    @InjectView(R.id.stopButton)
    private ImageButton stopButton;

    @InjectView(R.id.playerCard)
    private ImageView playerCardImageView;

    @InjectView(R.id.enemyCard)
    private ImageView enemyCardImageView;

    @InjectView(R.id.trump)
    private ImageView trumpImageView;

    @InjectView(R.id.cards)
    private TextView cardsTextView;

    @InjectView(R.id.enemyCards)
    private TextView enemyCardsTextView;

    private CardImagesLoader cardImagesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        GameStartedEvent event = (GameStartedEvent) intent.getSerializableExtra("event");
        gameId = event.getGameId();
        cards = event.getCards();
        trump = event.getTrump();
        cardImagesLoader = new CardImagesLoader(this);
        CardListAdapter adapter = new CardListAdapter(cards, cardImagesLoader);

        playerCardsListView.setAdapter(adapter);
        cardImagesLoader.hideCard(playerCardImageView);
        cardImagesLoader.hideCard(enemyCardImageView);
        cardImagesLoader.showCard(trumpImageView, trump);
    }

}