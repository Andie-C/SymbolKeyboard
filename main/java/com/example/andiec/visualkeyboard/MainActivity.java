package com.example.andiec.visualkeyboard;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*  Home screen containing:
    - a button to create a new keyboard,
    - a button to open a help prompt,
    - a list of all the names of the keyboards created.
 */
public class MainActivity extends AppCompatActivity
{
    // The actual list of keyboard names
    private List<String> mKeyboardNames;

    //To add a new keyboard, need to start the NewKeyboardActivity
    private static final int REQUEST_NEW_KEYBOARD = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Adding ActionBar on top of the screen
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Setting the layout of the activity
        setContentView(R.layout.activity_main);

        // Retrieving the names of the keyboards created
        loadKeyboardNames();

        // Building the button to add a new keyboard
        inflateAddButton();

        // Building the help button
        inflateHelpButton();

        // Building the button to delete a keyboard
        inflateDeleteButton();

        // Building the list of keyboard names
        inflateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        // Adding the ActionBar's items
        MenuItem loginItem = menu.findItem(R.id.login);
        loginItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        logoutItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Hiding login button if user is already logged in
        loginItem.setVisible(!User.get(this).isLoggedIn());
        // Hiding logout button if user is not logged in yet
        logoutItem.setVisible(User.get(this).isLoggedIn());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // When pressing the login button, start LoginRegisterActivity
            case R.id.login:
                startActivity(new Intent(this, LoginRegisterActivity.class));
                finish();
                return true;
            // When pressing the logout button, change user's status to "logged out" and refresh the screen
            case R.id.logout:
                User.get(this).logOut(this);
                refreshUI();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Iterates through the folders stored in the "keyboards" folder in memory,
        and stores the names of those folders in mKeyboardNames.
     */
    private void loadKeyboardNames()
    {
        mKeyboardNames = new ArrayList<>();

        File rootFolder = getDir("keyboards", MODE_PRIVATE);

        Collections.addAll(mKeyboardNames, rootFolder.list());
    }

    /*
        Sets the add button and adds an OnClickListener to it.
     */
    private void inflateAddButton()
    {
        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);

        // When pressing the add button, start activity to create a new keyboard.
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(MainActivity.this, NewKeyboardActivity.class),
                        REQUEST_NEW_KEYBOARD);
            }
        });
    }

    /*
        Sets the help button and adds an OnClickListener to it.
     */
    private void inflateHelpButton()
    {
        ImageButton helpButton = (ImageButton) findViewById(R.id.helpButton);

        // When pressing the help button, open a dialog containing some helper text.
        helpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.help_prompt);
                dialog.setTitle(R.string.help_prompt_title);

                // Inflating the button to close the dialog
                Button closeButton = (Button) dialog.findViewById(R.id.close_button);

                closeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    /*
        Sets the delete button and adds an OnClickListener to it.
     */
    private void inflateDeleteButton()
    {
        if(User.get(this).isLoggedIn())
        {
            ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);

            /*
                When pressing the delete button, open dialog for user to select the name of the
                keyboard to be deleted.
             */
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setTitle(R.string.delete_title);
                    dialog.setContentView(R.layout.delete_prompt);

                    RecyclerView keyboardsView = (RecyclerView) dialog.findViewById(R.id.recycler_view);

                    // Setting the type of layout in which the keyboards names will be displayed
                    keyboardsView.setLayoutManager(new LinearLayoutManager(dialog.getContext()));

                    // Setting the adapter which manages the names displayed on the RecyclerView
                    KeyboardAdapter adapter = new KeyboardAdapter(mKeyboardNames,HolderListener.DIALOG);
                    adapter.setDialog(dialog);
                    keyboardsView.setAdapter(adapter);

                    Button cancelButton = (Button)dialog.findViewById(R.id.KeyCancel);
                    // When pressing the cancel button, close the dialog and go back to the main screen.
                    cancelButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    /*
        Sets the view displaying the list of keyboards created.
     */
    private void inflateList()
    {
        //Using RecyclerView to only store the keyboard names which can be seen on screen
        RecyclerView mKeyboardsView = (RecyclerView) findViewById(R.id.recycler_view);
        mKeyboardsView.setHasFixedSize(true);

        // Setting the type of layout in which the keyboards names will be displayed
        mKeyboardsView.setLayoutManager(new LinearLayoutManager(this));

        // Setting the adapter which manages the names displayed on the RecyclerView
        mKeyboardsView.setAdapter(new KeyboardAdapter(mKeyboardNames, HolderListener.ACTIVITY));
    }


    /*
        Custom ViewHolder which contains a TextView to display the name of a keyboard.
     */
    private class KeyboardNameHolder extends RecyclerView.ViewHolder
    {
        TextView mKeyboardName;

        public KeyboardNameHolder(View itemView, View.OnClickListener listener)
        {
            super(itemView);

            mKeyboardName = (TextView) itemView.findViewById(R.id.name_textview);

            itemView.setOnClickListener(listener);
        }

        /*
            Links the name of the keyboard to the TextView.
         */
        public void bindName(String name)
        {
            mKeyboardName.setText(name);
        }
    }


    /*
        Custom OnClickListener which handles the event to be triggered when a user
        presses the name of a keyboard from the home screen or from a dialog.
     */
    private class HolderListener implements View.OnClickListener
    {
        // TextView storing the name of the keyboard pressed
        private TextView mKeyboardName;

        // Flag to know whether the user was on the home activity or on a dialog
        private int mContainerType;

        // Dialog, if any, on which the user pressed the name of the keyboard
        private Dialog mDeleteDialog;

        // Flags for the context on which the user pressed the name of the keyboard
        static final int ACTIVITY = 1, DIALOG = 2;

        public HolderListener(int containerType)
        {
            mContainerType = containerType;
        }

        public void setKeyboardName(TextView keyboardName)
        {
            mKeyboardName = keyboardName;
        }

        public void setDialog(Dialog dialog)
        {
            mDeleteDialog = dialog;
        }


        @Override
        public void onClick(View view)
        {
            /*
                When pressing a keyboard name from MainActivity,
                start activity to use the keyboard with the named pressed.
             */
            if(mContainerType == ACTIVITY)
            {
                Intent intent = new Intent(MainActivity.this, UseKeyboardActivity.class);
                intent.putExtra("name", mKeyboardName.getText().toString());
                startActivity(intent);
            }
            /*
                When pressing a keyboard name from the delete keyboard dialog,
                open confirmation dialog before deleting the appropriate keyboard.
             */
            else if(mContainerType == DIALOG)
            {
                showConfirmationDialog();
            }
        }

        /*
            Opens a dialog to ask the user for confirmation to delete
            the keyboard with the name pressed.
         */
        private void showConfirmationDialog()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            final String keyboardName = mKeyboardName.getText().toString();

            String message = getString(R.string.confirmation_description) + "\n"
                    + keyboardName;
            builder.setMessage(message)
                    .setTitle(R.string.confirmation_title);

            /*
                When pressing the cancel button, close the confirmation dialog and go back to
                the delete keyboard dialog.
             */
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            });

            /*
                When pressing the confirmation button, delete the files stored in the
                appropriate folder in internal memory, close the confirmation dialog,
                and show a status dialog to inform the user whether the deletion was successful.
             */
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
            {
                public void onClick(final DialogInterface dialog, int id)
                {
                    deleteKeyboard(keyboardName);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        /*
            Deletes all the images stored in the folder with the same name as the keyboard's name,
            then removes the folder itself.
         */
        private void deleteKeyboard(String keyboardName)
        {
            File folder = new File(getBaseContext().getDir("keyboards", MODE_PRIVATE)
                    .getAbsolutePath() + File.separator + keyboardName);

            boolean success = false;

            // Deleting the images contained in the folder
            for (File content : folder.listFiles())
                success = content.delete();

            // Deleting the folder itself
            if(success)
                success = folder.delete();

            // Setting the status message accordingly
            String statusMessage;
            if(success)
                statusMessage = keyboardName + " " + getString(R.string.delete_success);
            else
                statusMessage = getString(R.string.delete_error);

            showStatusDialog(statusMessage, success);
        }

        /*
            Show dialog with the status of the deletion of the keyboard.
         */
        private void showStatusDialog(String statusMessage, final boolean success)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(statusMessage);
            /*
                When pressing the close button, close the dialog and go back to either
                the home screen if the deletion was successful, or to the delete keyboard dialog
                it it was not.
             */
            builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    if(success)
                    {
                        mDeleteDialog.dismiss();
                        refreshUI();
                    }
                }
            });
            builder.create().show();
        }
    }

    /*
        Custom Adapter which contains the list of keyboards names,
        and handles the creation and deletion of KeyboardNameHolders when necessary.
     */
    private class KeyboardAdapter extends RecyclerView.Adapter<KeyboardNameHolder>
    {
        private List<String> mKeyboardNames;

        // Flag to know whether the user was on the home activity or on a dialog
        private int mListContainerType;

        // Dialog, if any, on which the user pressed the name of the keyboard
        private Dialog mDeleteDialog;

        public KeyboardAdapter(List<String> keyboardNames, int listContainerType)
        {
            mKeyboardNames = keyboardNames;
            mListContainerType = listContainerType;
        }

        public void setDialog(Dialog dialog)
        {
            mDeleteDialog = dialog;
        }

        @Override
        public KeyboardNameHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            // Setting the layout of the KeyboardNameHolder
            View view = inflater.inflate(R.layout.keyboard_name_layout, parent, false);

            HolderListener listener = new HolderListener(mListContainerType);
            KeyboardNameHolder holder = new KeyboardNameHolder(view, listener);
            listener.setKeyboardName(holder.mKeyboardName);
            listener.setDialog(mDeleteDialog);

            return holder;
        }

        @Override
        public void onBindViewHolder(KeyboardNameHolder holder, int position)
        {
            // Retrieving the name to be displayed, and binding it to its holder
            String name = mKeyboardNames.get(position);
            holder.bindName(name);
        }

        @Override
        public int getItemCount()
        {
            // Returning the number of elements in the list
            return mKeyboardNames.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*
            When resuming the app after finishing the NewKeyboardActivity, refresh the screen
            so the users can see the name of the last keyboard they created.
         */
        if (requestCode == REQUEST_NEW_KEYBOARD && resultCode == RESULT_OK)
        {
            refreshUI();
        }
    }

    private void refreshUI()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
