package com.example.andiec.visualkeyboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/*
    Screen allowing users to create a new keyboard.
 */
public class NewKeyboardActivity extends KeyboardActivity
{
    // Number of keys in the keyboard
    private static final int KEYS_COUNT = 14;

    // Flags to know which activity just finished before this activity was resumed.
    private static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_SAVED = 2;

    // Index of the key that the user pressed.
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setting the layout of the activity
        setContentView(R.layout.activity_new_keyboard);

        // Building the keys of the keyboard
        inflateKeys();

        // Building the save button
        inflateSaveKey();
    }

    /*
        Creates the keys of the keyboard, adds an OnClickListener to each button,
        and displays the keys on the screen.
     */
    private void inflateKeys()
    {
        mKeys = new ArrayList<>(KEYS_COUNT);

        // Creating Key objects for each key of the keyboard
        for (int i=0;i<KEYS_COUNT;i++)
        {
            mKeys.add(i, new Key(this));

            /*
                When pressing a key, open dialog for user to choose which application to use
                to customise the image of the key (photo gallery or camera).
             */
            mKeys.get(i).getButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    for(int j=0;j<mKeys.size();j++)
                    {
                        if(mKeys.get(j).getButton().equals(view))
                        {
                            // Store the index of the key pressed
                            mIndex = j;
                            break;
                        }
                    }

                    Dialog imageDialog = new Dialog(NewKeyboardActivity.this);
                    imageDialog.setTitle(R.string.image_prompt_title);
                    imageDialog.setContentView(R.layout.image_prompt);
                    setDialogButtons(imageDialog);
                    imageDialog.show();
                }
            });
        }

        super.displayKeys();
    }

    /*
        Sets the buttons that the user can press to open the photo gallery or the camera.
     */
    private void setDialogButtons(final Dialog dialog)
    {
        Button galleryButton = (Button) dialog.findViewById(R.id.gallery_button);
        /*
            When pressing the gallery button, open Photos app so the user can pick
            an image from the device's gallery.
         */
        galleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openGalleryIntent.setType("image/*");

                startActivityForResult(openGalleryIntent, REQUEST_IMAGE_SAVED);
                dialog.dismiss();
            }
        });

        Button cameraButton = (Button) dialog.findViewById(R.id.camera_button);
        /*
            When pressing the gallery button, open Camera app so the user can take a picture.
         */
        cameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            When resuming the app after closing the Camera app, retrieve the picture taken,
            and open a dialog to ask the user for the word associated to the image.
         */
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            textPrompt(imageBitmap);
        }
        /*
            When resuming the app after closing the Photos app, retrieve the picture selected,
            and open the dialog to ask the user for the word associated to the image.
         */
        else if(requestCode == REQUEST_IMAGE_SAVED && resultCode == RESULT_OK)
        {
            Uri chosenImageUri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenImageUri);
                textPrompt(bitmap);
            }
            catch (IOException ioe)
            {
                Log.e("NewKeyboardActivity", ioe.getMessage() + Arrays.toString(ioe.getStackTrace()));
            }
        }
    }

    /*
        Opens a dialog with a text field so the user can enter
        the word associated to the key they are customising.
     */
    private void textPrompt(final Bitmap bitmapDrawable)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.dialog_title);
        dialog.setContentView(R.layout.text_prompt);

        TextView textView = (TextView)dialog.findViewById(R.id.TextView);
        textView.setText(R.string.dialog_description);

        final EditText textField = (EditText)dialog.findViewById(R.id.ImageMeaning);
        textField.setHint(R.string.dialog_hint);
        // Setting the max length of the text field to 16 characters
        textField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});

        Button cancelButton = (Button)dialog.findViewById(R.id.KeyCancel);

        // When pressing the cancel button, close the dialog and go back to the NewKeyboard screen.
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.cancel();
            }
        });

        Button saveButton = (Button)dialog.findViewById(R.id.KeySave);
        /*
            When pressing the save button, store the image and the label in the key,
            and close the dialog.
         */
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(textField.getText().toString().isEmpty())
                {
                    // If the label field is empty, show error message on the dialog
                    TextView textView = (TextView)dialog.findViewById(R.id.error);
                    textView.setText(R.string.dialog_empty);
                    textView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mKeys.get(mIndex).setButtonImage(bitmapDrawable);
                    mKeys.get(mIndex).getLabel().setText(textField.getText());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    /*
        Sets the save button and adds an OnClickListener to it.
     */
    private void inflateSaveKey()
    {
        Button saveButton = (Button)findViewById(R.id.KeyboardSave);

        // When pressing the save button, open a dialog to ask the user for the name of the keyboard.
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                savePrompt();
            }
        });
    }

    /*
        Opens a dialog with a text field so the user can enter the name of the keyboard.
     */
    private void savePrompt()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.title_keyboard);
        dialog.setContentView(R.layout.text_prompt);

        TextView textView = (TextView)dialog.findViewById(R.id.TextView);
        textView.setText(R.string.name_keyboard);

        final EditText textField = (EditText)dialog.findViewById(R.id.ImageMeaning);
        textField.setHint(R.string.hint_keyboard);
        // Setting the max length of the text field to 30 characters
        textField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});

        Button cancelButton = (Button)dialog.findViewById(R.id.KeyCancel);
        // When pressing the cancel button, close the dialog and go back to the NewKeyboard screen.
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.cancel();
            }
        });

        Button saveButton = (Button)dialog.findViewById(R.id.KeySave);
        /*
            When pressing the save button, save keyboard in memory and finish this activity.
         */
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(textField.getText().toString().isEmpty())
                {
                    // If the keyboard's name field is empty, show error message on the dialog
                    TextView textView = (TextView)dialog.findViewById(R.id.error);
                    textView.setText(R.string.empty_keyboard);
                    textView.setVisibility(View.VISIBLE);
                }
                else if(saveKeyboard(textField.getText().toString(),dialog))
                {
                    setResult(RESULT_OK);
                    NewKeyboardActivity.this.finish();
                }
            }
        });

        dialog.show();
    }

    /*
        Saves the keyboard in the "keyboards" folder in internal memory by creating
        a folder with the same name as the keyboard's name,
        and storing each key image in a .png file with the same name as
        the word associated to the key.
     */
    private boolean saveKeyboard(String name, Dialog dialog)
    {
        // Getting path to keyboards folder storing all the keyboards
        File folder = getBaseContext().getDir("keyboards", MODE_PRIVATE);

        // Listing all the folders in "keyboards" folders
        String[] contentFolder = folder.list();

        // Checking if there already is a folder with the same name as the new keyboard
        if(contentFolder.length > 0)
        {
            for(String childFolder : contentFolder)
            {
                if(name.equals(childFolder))
                {
                    // If the name was already found, show error message on the dialog
                    TextView textView = (TextView)dialog.findViewById(R.id.error);
                    if (textView == null)
                        return false;

                    textView.setText(R.string.error_keyboard);
                    textView.setVisibility(View.VISIBLE);
                    return false;
                }
            }
        }

        // Creating new folder inside "keyboards" folder
        File newFolder = new File(folder.getAbsolutePath() + File.separator + name);
        if(newFolder.mkdir())
        {
            FileOutputStream fileStream = null;
            try
            {
                try
                {
                    // For each key, compressing the image and storing it in a .png file inside the new folder
                    for(int i=0;i<mKeys.size();i++)
                    {
                        Bitmap bitmap = mKeys.get(i).getImage();
                        if(bitmap != null)
                        {
                            fileStream = new FileOutputStream(newFolder.getAbsolutePath() + File.separator + mKeys.get(i).getLabel().getText());
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileStream);
                        }
                    }

                    // Closing the dialog once all the images are saved
                    dialog.dismiss();
                    return true;
                }
                finally
                {
                    if(fileStream != null)
                        fileStream.close();
                }
            }
            catch (IOException ioe)
            {
                Log.e("NewKeyboardActivity", ioe.getMessage() + Arrays.toString(ioe.getStackTrace()));
            }
        }
        return false;
    }
}