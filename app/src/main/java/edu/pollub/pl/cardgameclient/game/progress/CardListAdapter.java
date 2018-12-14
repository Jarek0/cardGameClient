package edu.pollub.pl.cardgameclient.game.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import java.util.List;

import edu.pollub.pl.cardgameclient.R;
import model.Card;

class CardListAdapter extends ArrayAdapter<Card> implements View.OnClickListener {

    private CardImagesLoader imagesLoader;

    private CardMoveCallback cardMove;

    private static class ViewHolder {
        ImageButton card;
    }

    CardListAdapter(List<Card> cards, CardImagesLoader imagesLoader, CardMoveCallback cardMove) {
        super(imagesLoader.getContext(), R.layout.card_item, cards);
        this.imagesLoader = imagesLoader;
        this.cardMove = cardMove;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Card card = (Card) object;
        System.out.println(card);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Card card = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(imagesLoader.getContext());
            convertView = inflater.inflate(R.layout.card_item, parent, false);
            viewHolder.card = convertView.findViewById(R.id.card);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imagesLoader.showCard(viewHolder.card, card);
        initCard(position, card, viewHolder);

        return convertView;
    }

    private void initCard(int position, Card card, ViewHolder viewHolder) {
        viewHolder.card.setTag(position);
        if(cardMove.canCardBeUsed(card)) {
            viewHolder.card.setImageAlpha(255);
            viewHolder.card.setEnabled(true);
            viewHolder.card.setOnClickListener(v -> cardMove.useCard(card));
        }
        else {
            viewHolder.card.setImageAlpha(75);
            viewHolder.card.setEnabled(false);
        }
    }
}
