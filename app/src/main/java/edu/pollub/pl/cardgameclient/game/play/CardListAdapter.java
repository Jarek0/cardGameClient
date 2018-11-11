package edu.pollub.pl.cardgameclient.game.play;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import edu.pollub.pl.cardgameclient.R;
import model.Card;

public class CardListAdapter extends ArrayAdapter<Card> implements View.OnClickListener {

    private List<Card> cards;
    private CardImagesLoader imagesLoader;

    private static class ViewHolder {
        ImageView cardView;
    }

    public CardListAdapter(List<Card> cards, CardImagesLoader imagesLoader) {
        super(imagesLoader.getContext(), R.layout.card_item, cards);
        this.cards = cards;
        this.imagesLoader = imagesLoader;
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
            viewHolder.cardView = convertView.findViewById(R.id.card);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imagesLoader.showCard(viewHolder.cardView, card);
        viewHolder.cardView.setTag(position);

        return convertView;
    }
}
