package com.example.justin.symbilityintersecthearthstone;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Tests for the CardAdapter class.
 */
@RunWith(AndroidJUnit4.class)
public class CardAdapterTest {

    private Context appContext;
    private CardAdapter cardAdapter;
    private ArrayList<Card> cards;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    /** Tests for getCount() method. */
    @Test
    public void testGetCount() {
        cards = new ArrayList<>();
        cardAdapter = new CardAdapter(appContext, cards);
        assertEquals(cardAdapter.getCount(), 0);

        cards.add(new Card("myName", "myType", "myClass", "myUrl"));
        cardAdapter = new CardAdapter(appContext, cards);
        assertEquals(cardAdapter.getCount(), 2);
    }

    /** Tests for getCount() method, where the cards are null. */
    @Test
    public void testGetCountNull() {
        cards = null;
        cardAdapter = new CardAdapter(appContext, cards);
        assertEquals(cardAdapter.getCount(), 0);
    }

    /** Tests for getItemId() method. */
    @Test
    public void testGetItemId() {
        cards = new ArrayList<>();
        cardAdapter = new CardAdapter(appContext, cards);
        assertEquals(cardAdapter.getItemId(7), 7);
    }

    /** Tests for getItem() method. */
    @Test
    public void testGetItem() {
        cards = new ArrayList<>();
        cards.add(new Card("myName", "myType", "myClass", "myUrl"));
        cards.add(new Card("myName2", "myType2", "myClass2", "myUrl2"));
        cardAdapter = new CardAdapter(appContext, cards);

        assertEquals(((Card) cardAdapter.getItem(0)).getName(), "myName");
        assertEquals(((Card) cardAdapter.getItem(1)).getName(), "myName2");

        assertEquals(((Card) cardAdapter.getItem(0)).getType(), "myType");
        assertEquals(((Card) cardAdapter.getItem(1)).getType(), "myType2");

        assertEquals(((Card) cardAdapter.getItem(0)).getPlayerClass(), "myClass");
        assertEquals(((Card) cardAdapter.getItem(1)).getPlayerClass(), "myClass2");

        assertEquals(((Card) cardAdapter.getItem(0)).getImgURL(), "myUrl");
        assertEquals(((Card) cardAdapter.getItem(1)).getImgURL(), "myUrl2");
    }

    /** Tests for getView() method on even positions. */
    @Test
    public void testGetViewEvenPosition() {
        cards = new ArrayList<>();
        cards.add(new Card("myName", "myType", "myClass", "myUrl"));
        cardAdapter = new CardAdapter(appContext, cards);

        View view = cardAdapter.getView(0, null, null);

        assertEquals(view.getLayoutParams().height, 500);
        assertNotNull(view.findViewById(R.id.card_image));
    }


    /** Tests for getView() method on odd positions. */
    @Test
    public void testGetViewOddPosition() {
        cards = new ArrayList<>();
        cards.add(new Card("myName", "myType", "myClass", "myUrl"));
        cardAdapter = new CardAdapter(appContext, cards);

        View view = cardAdapter.getView(1, null, null);

        assertEquals(view.getLayoutParams().height, 500);
        assertNotNull(view.findViewById(R.id.card_name));
        assertNotNull(view.findViewById(R.id.card_type));
        assertNotNull(view.findViewById(R.id.card_player_class));

        assertEquals(((TextView) view.findViewById(R.id.card_name)).getText(), cards.get(0).getName());
        assertEquals(((TextView) view.findViewById(R.id.card_type)).getText(), cards.get(0).getType());
        assertEquals(((TextView) view.findViewById(R.id.card_player_class)).getText(), cards.get(0).getPlayerClass());
    }
}
