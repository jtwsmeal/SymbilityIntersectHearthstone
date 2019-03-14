package com.example.justin.symbilityintersecthearthstone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/** The class associated with the activity which displays card-related data. */
public class CardData extends AppCompatActivity {

    /** The default request URL for the card data. */
    final String DEFAULT_URL = "https://omgvamp-hearthstone-v1.p.rapidapi.com/cards";

    /** The request URL prefix for searching specific cards. */
    final String SEARCH_URL = DEFAULT_URL + "/search/";

    /** The API key data. */
    final String[] API_KEY = {"X-RapidAPI-Key", "746b08a8f3msh24740127eda7ad0p1b05c2jsn99887cc7f961"};

    /** The GridView that displays the card data. */
    GridView cardGrid;

    /** The tag used for logging. */
    private static final String TAG = "CardData";

    /** The search box on the activity toolbar. */
    SearchView searchBox;

    /** A loading bar to display while the data loads. */
    ProgressBar loadBar;

    /**
     * Loads the activity.
     *
     * @param savedInstanceState The activity's saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_data);

        // Asynchronously retrieve the card data.
        getCardData();

        cardGrid = (GridView) findViewById(R.id.card_grid);
        searchBox = (SearchView) findViewById(R.id.search_box);
        loadBar = (ProgressBar) findViewById(R.id.load_bar);

        cardGrid.setVisibility(View.INVISIBLE);
        searchBox.setVisibility(View.INVISIBLE);
        loadBar.setVisibility(View.VISIBLE);

        // Listener for query completion.
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Performs a REST call when a query is submitted.
             *
             * @param query The query to search
             * @return true to indicate that the listener will handle the query.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                getCardData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        searchBox.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            /**
             * Obtains data on all cards when search box loses focus.
             *
             * @param view The View that had a change in focus.
             * @param hasFocus True iff the search box now has focus.
             */
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    cardGrid.setVisibility(View.INVISIBLE);
                    searchBox.setVisibility(View.INVISIBLE);
                    loadBar.setVisibility(View.VISIBLE);
                    getCardData();
                }
            }
        });
    }

    /**
     * Asynchronously retrieve card data for the specified card, or every card if query is null.
     *
     * @param query the name of the card to search, or null if you want every card
     */
    public void getCardData(String query) {

        /* If given a null query, pass in null to the AsyncTask to use the default URL.
         * Otherwise, append the query to the search URL prefix.
         */
        if (query != null) {
            query = SEARCH_URL + query;
        }

        CardDataRetriever retriever = new CardDataRetriever();
        retriever.execute(query);
    }

    /** Asynchronously retrieve all card data. */
    public void getCardData() {
        getCardData(null);
    }

    /**
     * Returns a card made from a given JSON object.
     *
     * @param cardJson The JSON card object
     * @return A card based on the given JSON object.
     * @throws JSONException When a parsing error occurs.
     */
    public Card getCardFromJson(JSONObject cardJson) throws JSONException {

        String name, type, playerClass, imgURL;

        /* Set defaults if a JSON property doesn't exist.  This is mainly used for the
         * purpose of having a placeholder image for cards that don't have an image.
         */
        if (cardJson.has("name")) {
            name = cardJson.getString("name");
        } else {
            name = null;
        }

        if (cardJson.has("type")) {
            type = cardJson.getString("type");
        } else {
            type = null;
        }

        if (cardJson.has("playerClass")) {
            playerClass = cardJson.getString("playerClass");
        } else {
            playerClass = null;
        }

        if (cardJson.has("img")) {
            imgURL = cardJson.getString("img");
        } else {
            imgURL = null;
        }

        return new Card(name, type, playerClass, imgURL);
    }

    /**
     * Obtains the required information from the given JSON.
     * Call this method to parse JSON containing data about all cards (when DEFAULT_URL is used).
     *
     * @param body The JSON body to parse
     * @return A list containing card data
     * @throws JSONException When a parsing error occurs
     */
    public ArrayList<Card> parseJsonBodyAllCards(JSONObject body) throws JSONException {

        ArrayList<Card> cards = new ArrayList<>();
        Iterator<String> cardSets = body.keys();

        while (cardSets.hasNext()) {
            JSONArray cardSet = body.getJSONArray(cardSets.next());

            // Ignore credits data.
            if (cardSet.length() > 0 && cardSet.getJSONObject(0).get("cardSet").equals("Credits")) {
                continue;
            }

            for (int card = 0; card < cardSet.length(); card++) {
                JSONObject cardJson = cardSet.getJSONObject(card);

                // Make the new card and add it to the list.
                Card newCard = getCardFromJson(cardJson);

                cards.add(newCard);
            }
        }

        Log.d(TAG, "Obtained all cards.");

        return cards;
    }

    /**
     * Obtains the required information from the given JSON.
     * Call this method to parse JSON obtained from a specific card search.
     *
     * @param results The JSON search results to parse
     * @return A list containing card data
     * @throws JSONException When a parsing error occurs
     */
    public ArrayList<Card> parseJsonBodyCardSearch(JSONArray results) throws JSONException {

        ArrayList<Card> cards = new ArrayList<>();

        for (int result = 0; result < results.length(); result++) {
            JSONObject cardJson = results.getJSONObject(result);
            Card newCard = getCardFromJson(cardJson);
            cards.add(newCard);
        }

        Log.d(TAG, "Obtained all cards.");

        return cards;
    }

    /** An AsyncTask subclass used to asynchronously call a REST API. */
    private class CardDataRetriever extends AsyncTask<String, Integer, CardAdapter> {

        /** Calls a REST API asynchronously.
         *
         * @param urls The request url.  If null then use the default url.
         * @return The card GridView adapter
         */
        @Override
        protected CardAdapter doInBackground(String... urls) {

            ArrayList<Card> results;
            String url;
            CardAdapter cardAdapter = null;
            HttpResponse<JsonNode> response = null;

            // If the given URL is null, use the default URL to obtain all images.
            if (urls[0] == null) {
                url = DEFAULT_URL;
            } else {
                url = urls[0];
            }

            try {
                response = Unirest.get(url)
                        .header(API_KEY[0], API_KEY[1])
                        .asJson();
            } catch (UnirestException je) {
                Log.e(TAG, "Error:  Request has failed:  " + je.getMessage());
            }

            if (response != null) {

                try {

                    // Parse the JSON object.
                    if (urls[0] == null) {
                        JSONObject body = response.getBody().getObject();
                        results = CardData.this.parseJsonBodyAllCards(body);
                    } else {
                        JSONArray body = response.getBody().getArray();
                        results = CardData.this.parseJsonBodyCardSearch(body);
                    }
                    cardAdapter = new CardAdapter(CardData.this, results);

                } catch (JSONException je) {
                    Log.e(TAG, "Error while parsing JSON:  " + je.getMessage());
                }

            } else {
                Log.e(TAG, "Error:  JSON body is null");
            }

            return cardAdapter;
        }

        /** Fills in the grid once the asynchronous request has completed.
         *
         * @param cardAdapter The card adapter for the grid
         */
        @Override
        protected void onPostExecute(CardAdapter cardAdapter) {
            cardGrid.setVisibility(View.VISIBLE);
            searchBox.setVisibility(View.VISIBLE);
            loadBar.setVisibility(View.INVISIBLE);

            Log.d(TAG, "Filling in the GridView");
            cardGrid.setAdapter(cardAdapter);
        }
    }
}