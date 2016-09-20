package com.example.andiec.visualkeyboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


/*
    Screen with a keyboard and a TextView which is updated for every key press.
 */
public class UseKeyboardActivity extends KeyboardActivity
{
    // Folder containing the images of the keyboard to be used
    private File mKeyboardFolder;

    // TextView containing the words corresponding to the keys pressed
    private TextView mDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setting the layout of the activity
        setContentView(R.layout.activity_use_keyboard);

        // Retrieving the name of the keyboard to be used
        String name = getIntent().getStringExtra("name");

        // Getting the path to the folder with the same name as the keyboard to be used
        mKeyboardFolder = new File(getBaseContext().getDir("keyboards", MODE_PRIVATE)
                .getAbsolutePath() + File.separator + name);

        // Building the keys of the keyboard
        inflateKeys();
    }

    /*
        Loads the images from the memory and rebuilds the saved keyboard.
     */
    private void inflateKeys()
    {
        // Listing all the files stored in the keyboard's folder
        File[] contentFolder = mKeyboardFolder.listFiles();

        mDisplay = (TextView) findViewById(R.id.display);

        // List of keys of the keyboard
        mKeys = new ArrayList<>(contentFolder.length);

        /*
            For each image stored in the folder, decompress the image and store it in a key,
            and set key's label to the name of the image.
         */
        for(File folder : contentFolder)
        {
            final Key key = new Key(this);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(folder.getAbsolutePath(), options);
            key.setButtonImage(bitmap);

            key.setLabel(folder.getName());

            // When pressing a key, add the key's label at the end of the TextView.
            key.getButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mDisplay.setText(mDisplay.getText() + "   " + key.getLabel().getText());
                }
            });

            mKeys.add(key);
        }

        ImageButton clearButton = (ImageButton) findViewById(R.id.clear_button);
        // When pressing the clear button, empty the TextView.
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mDisplay.setText("");
            }
        });

        super.displayKeys();
    }
}
