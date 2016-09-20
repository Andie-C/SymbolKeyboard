package com.example.andiec.visualkeyboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;


/*
    Each key in the keyboard is made up of
    - an ImageButton containing the picture for the key,
    - a TextView containing the word associated to the picture.
 */
public class Key
{
    // TextView containing the label of the key
    private TextView mLabel;

    // Actual picture for the key.
    private Bitmap mImage;

    // Button containing the key's image.
    private ImageButton mButton;

    // Activity which requested the creation of the key.
    private Activity mActivity;


    public Key(Activity activity)
    {
        mActivity = activity;

        mLabel = new TextView(activity);
        mButton = new ImageButton(activity);

        // Centering the label in the key's layout
        mLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Setting a fixed size to the ImageButton
        mButton.setLayoutParams(new TableRow.LayoutParams(242, 194));
        // Adding shadow to button
        mButton.setBackground(ContextCompat.getDrawable(mActivity,android.R.drawable.dialog_holo_dark_frame));
        // Setting default image
        mButton.setImageResource(R.drawable.default_image);
        // Scaling the image in the button
        mButton.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    /*
        Returns the key's TextView.
     */
    public TextView getLabel()
    {
        return mLabel;
    }

    /*
        Returns the key's Bitmap image.
     */
    public Bitmap getImage()
    {
        return mImage;
    }

    /*
        Returns the key's ImageButton.
     */
    public ImageButton getButton()
    {
        return mButton;
    }

    /*
        Sets the text of the key's TextView to the given label.
     */
    public void setLabel(String label)
    {
        mLabel.setText(label);
    }

    /*
        Sets the image of the key's ImageButton to the given image.
     */
    public void setButtonImage(Bitmap image)
    {
        mImage = image;

        // Converting the bitmap into a drawable
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mActivity.getResources(), image);

        mButton.setImageDrawable(bitmapDrawable);
    }
}
