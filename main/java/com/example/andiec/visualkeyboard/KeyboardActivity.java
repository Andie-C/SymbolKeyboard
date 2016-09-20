package com.example.andiec.visualkeyboard;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;


/*
    Variables and methods common to all the activities containing a keyboard.
 */
public abstract class KeyboardActivity extends AppCompatActivity
{
    // List of the keyboard keys.
    List<Key> mKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Adding action bar with up button to go back to home screen.
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Setting the keys of the keyboard.
     */
    void displayKeys()
    {
       // Keyboard layout
        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outer_layout);

        // TableLayouts representing each row of keys
        TableLayout top, mid, bottom;
        // Actual row of keys for each TableLayout
        TableRow topRow, midRow, bottomRow;

        top = (TableLayout) outerLayout.findViewById(R.id.top_row);
        topRow = new TableRow(this);

        mid = (TableLayout) outerLayout.findViewById(R.id.mid_row);
        midRow = new TableRow(this);

        bottom = (TableLayout) outerLayout.findViewById(R.id.bot_row);
        bottomRow = new TableRow(this);

        for (int i=0;i<mKeys.size();i++)
        {
            // Vertical layout containing one key (i.e. one TextView and one ImageButton)
            LinearLayout keyLayout = new LinearLayout(this);
            keyLayout.setOrientation(LinearLayout.VERTICAL);

            // Setting the width and height of the keyLayout
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            keyLayout.setLayoutParams(params);

            // Adding the TextView of the key to the keyLayout
            keyLayout.addView(mKeys.get(i).getLabel());

            // Adding the button to the keyLayout
            keyLayout.addView(mKeys.get(i).getButton());

            /*
                The first 5 keys are added to the topRow, the next 4 to the middleRow,
                and the last 5 to the bottomRow.
             */
            if (i<5)
                topRow.addView(keyLayout);
            else if (i<9)
                midRow.addView(keyLayout);
            else
                bottomRow.addView(keyLayout);
        }

        // Adding each TableRow to its corresponding TableLayout
        top.addView(topRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        mid.addView(midRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        bottom.addView(bottomRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}
