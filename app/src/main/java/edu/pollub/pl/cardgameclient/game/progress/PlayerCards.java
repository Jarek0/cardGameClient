package edu.pollub.pl.cardgameclient.game.progress;

import org.lucasr.twowayview.TwoWayView;

import java.util.List;

import model.Card;

class PlayerCards {

    private final TwoWayView playerCardsListView;

    private final CardImagesLoader cardImagesLoader;

    private List<Card> cards;

    private CardMoveCallback cardMove;

    PlayerCards(CardImagesLoader cardImagesLoader, TwoWayView playerCardsListView,  List<Card> cards, CardMoveCallback cardMove) {
        this.cardImagesLoader = cardImagesLoader;
        this.playerCardsListView = playerCardsListView;
        showCards(cards, cardMove);
    }

    void removeCard(Card attackCard, CardMoveCallback cardMove) {
        cards.remove(attackCard);
        showCards(cards, cardMove);
    }

    void changeMove(CardMoveCallback defenseMove) {
        showCards(cards, defenseMove);
    }

    void changeCards(List<Card> newCards, CardMoveCallback cardMove) {
        cards = newCards;
        showCards(cards, cardMove);
    }

    private void showCards(List<Card> cards, CardMoveCallback cardMove) {
        this.cards = cards;
        this.cardMove = cardMove;
        CardListAdapter adapter = new CardListAdapter(cards, cardImagesLoader, cardMove);
        playerCardsListView.setAdapter(adapter);
    }
}
