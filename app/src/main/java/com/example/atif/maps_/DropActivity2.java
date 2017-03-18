package com.example.atif.maps_;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

public class DropActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_dialog);

        /*LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.drop_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(DropActivity2.this);
        builder.setView(dialoglayout);

        SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        swipeSelector.setItems(
                new SwipeItem(0, "Police Investigation", "Description for slide one."),
                new SwipeItem(1, "Sick Passenger", "Description for slide two."),
                new SwipeItem(2, "Train Traffic", "Description for slide three."),
                new SwipeItem(3, "Signal Malfunction", "Description for slide four.")

        );


        SwipeSelector swipeSelector2 = (SwipeSelector) findViewById(R.id.swipeSelector2);
        swipeSelector2.setItems(
                new SwipeItem(0, "1 Train", "Description for slide one."),
                new SwipeItem(1, "2 Train", "Description for slide two."),
                new SwipeItem(2, "3 Train", "Description for slide three."),
                new SwipeItem(3, "4 Train", "Description for slide four.")
        );

        SwipeSelector swipeSelector3 = (SwipeSelector) findViewById(R.id.swipeSelector3);
        swipeSelector3.setItems(
                new SwipeItem(0, "Uptown", "Description for slide one."),
                new SwipeItem(1, "Downtown", "Description for slide two.")

        );
    }*/

    }
}
