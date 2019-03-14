package com.example.justin.symbilityintersecthearthstone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


/**
 * An adapter used to fill in the GridView of card data.
 */
public class CardAdapter extends BaseAdapter {

    /** The tag used for logging. */
    private static final String TAG = "CardAdapter";

    /** The number of columns in the GridView. */
    private final int NUM_COLS = 2;

    /** The application context. */
    private final Context context;

    /** The list of card data. */
    private final ArrayList<Card> cards;

    /**
     * Default CardAdapter constructor.
     *
     * @param context The application context
     * @param cards The list of card data
     */
    public CardAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    /**
     * Returns the number of grid cells to display.
     * Note that this value is twice the number of cards, since each card requires 2 cells.
     *
     * @return The number of cards to display
     */
    @Override
    public int getCount() {
        if (cards != null) {
            return NUM_COLS * cards.size();
        } else {
            Log.d(TAG, "Error: Could not calculate the number of cards because the array is null.");
            return 0;
        }
    }

    /**
     * Returns the ID of the current item.
     *
     * @param position The item's position
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the card located at index position.
     *
     * @param position The index of the card to get
     * @return The card at index position
     */
    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    /**
     * Inflates and returns the view inside a grid cell.
     *
     * @param position The index of the grid cell
     * @param view The view to inflate
     * @param viewGroup The parent view
     * @return The inflated view
     */
    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        // The card to display is at index (position / 2) because each card requires two grid cells.
        final Card card = cards.get(position / NUM_COLS);

        Log.d(TAG, "Inflating view for card " + card.getName());

        TextView cardName, cardType, cardPlayerClass;
        ImageView cardImage;

        final LayoutInflater layoutInflater = LayoutInflater.from(context);

        // If it is a left grid cell, insert an image. Otherwise, use card_info layout.
        if (position % NUM_COLS == 0) {
            view = layoutInflater.inflate(R.layout.card_image, null);

            cardImage = view.findViewById(R.id.card_image);

            // Asynchronously set the card's image.
            CardImageRetriever retriever = new CardImageRetriever(cardImage);
            retriever.execute(card.getImgURL());

        } else {
            view = layoutInflater.inflate(R.layout.card_info, null);

            cardName = view.findViewById(R.id.card_name);
            cardType = view.findViewById(R.id.card_type);
            cardPlayerClass = view.findViewById(R.id.card_player_class);

            cardName.setText(card.getName());
            cardType.setText(card.getType());
            cardPlayerClass.setText(card.getPlayerClass());
        }

        // Sets the inflated views to have the same heights.
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 500));

        return view;
    }


    /** An AsyncTask subclass used to asynchronously obtain an image from a given URL. */
    private class CardImageRetriever extends AsyncTask<String, Void, Bitmap> {

        ImageView cardImage;

        /**
         * The default constructor.
         *
         * @param cardImage The ImageView to fill
         */
        private CardImageRetriever(ImageView cardImage) {
            this.cardImage = cardImage;
        }

        /**
         * Obtains the required image asynchronously.
         *
         * @param urls The image URL
         * @return The read bitmap
         */
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap img = null;

            /* We can return a null image if the image URL is not
             * specified, so that the placeholder image will be used.
             */
            if (urls[0] != null) {
                try {
                    InputStream inputStream = new URL(urls[0]).openStream();
                    img = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    Log.e(TAG, "Error while parsing bitmap:  " + e.getMessage());
                }
            }

            return img;
        }

        /**
         * Fills in the ImageView once the asynchronous request has completed.
         * Uses a placeholder image if the given bitmap is null.
         *
         * @param result The bitmap to load in
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                cardImage.setImageBitmap(result);
            } else {
                cardImage.setImageResource(R.drawable.empty_card);
            }
        }
    }
}
