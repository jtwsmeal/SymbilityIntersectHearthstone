package com.example.justin.symbilityintersecthearthstone;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


/** The class associated with the splash screen. */
public class SplashScreen extends AppCompatActivity {

    /** The tag used for logging. */
    private static final String TAG = "CardData";

    /**
     * Loads the activity.
     *
     * @param savedInstanceState The activity's saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView dateText = (TextView) findViewById(R.id.date);
        ConstraintLayout background = (ConstraintLayout) findViewById(R.id.splash_screen);

        // Obtain the date and set the TextView on the splash screen to display the date.
        DateFormat dateFormat = DateFormat.getDateInstance();
        Date date = new Date();
        dateText.setText(dateFormat.format(date));

        // Create a listener to go to the CardData activity from this splash screen.
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Now opening the Card Data activity.");
                startActivity(new Intent(SplashScreen.this, CardData.class));
            }
        });
    }



}
