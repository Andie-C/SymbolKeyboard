package com.example.andiec.visualkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/*
    Screen allowing users to register or login.
 */
public class LoginRegisterActivity extends AppCompatActivity
{
    private User mUser;

    // Stores true if the user has already created an account, false otherwise
    private boolean mUserRegistered;

    private EditText mUsernameField, mEmailField, mPasswordField, mConfirmPasswordField;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setting the layout of the activity
        setContentView(R.layout.activity_login_register);

        mUser = User.get(this);

        // Check if the user had already created an account
        if(!"".equals(mUser.getUsername()))
            mUserRegistered = true;

        // Building the text fields that the user has to fill to login or register
        inflateTextFields();

        // Building the button to login or register
        inflateButton();
    }

    /*
        Sets the labels based on whether the user wants to register or login,
        and sets the text fields that the user has to fill.
     */
    private void inflateTextFields()
    {
        TextView titleView = (TextView) findViewById(R.id.user_title);

        mUsernameField = (EditText) findViewById(R.id.username_field);

        mPasswordField = (EditText) findViewById(R.id.password_field);

        // User wants to login
        if(mUserRegistered)
        {
            titleView.setText(R.string.login_description);
            mUsernameField.setText(mUser.getUsername());
        }
        // User wants to register
        else
        {
            titleView.setText(R.string.register_description);
            mEmailField = (EditText) findViewById(R.id.email_field);

            // Showing email fields and confirm password fields
            mEmailField.setVisibility(View.VISIBLE);
            mConfirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);
            mConfirmPasswordField.setVisibility(View.VISIBLE);
        }
    }

    /*
        Sets login/register button and adds an OnClickListener to it.
     */
    private void inflateButton()
    {
        Button confirmButton = (Button) findViewById(R.id.confirm_button);

        // Setting the button's text to login or register appropriately
        if(mUserRegistered)
            confirmButton.setText(R.string.login);
        else
            confirmButton.setText(R.string.register);

        /*
            When pressing the button, check that the input is correct and create a new user
            or compare the input to the details of the existing user to log them in
         */
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(inputIsValid())
                {
                    // User wants to register
                    if(!mUserRegistered)
                    {
                        mUser.setUsername(LoginRegisterActivity.this, mUsernameField.getText().toString());
                        mUser.setEmail(LoginRegisterActivity.this, mEmailField.getText().toString());
                        mUser.setPassword(LoginRegisterActivity.this, mPasswordField.getText().toString());
                    }

                    // Log user in
                    mUser.logIn(LoginRegisterActivity.this);

                    // Go back to home screen
                    startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
                    LoginRegisterActivity.this.finish();
                }
            }
        });
    }

    /*
        Checks that all the text fields were filled, and that the input matches the saved details
        if the user is trying to log in
     */
    private boolean inputIsValid()
    {
        TextView errorView = (TextView) findViewById(R.id.error);
        errorView.setVisibility(View.VISIBLE);

        // Checking that the username field is not empty
        if("".equals(mUsernameField.getText().toString()))
        {
            errorView.setText(R.string.empty_username_error);
            return false;
        }
        // Checking that the password field is not empty
        if("".equals(mPasswordField.getText().toString()))
        {
            errorView.setText(R.string.empty_password_error);
            return false;
        }
        if(mUserRegistered)
        {
            // Comparing the username entered to the saved one
            if(!mUser.getUsername().equals(mUsernameField.getText().toString()))
            {
                errorView.setText(R.string.wrong_username_error);
                return false;
            }
            // Comparing the password entered to the saved one
            if(!mUser.checkPassword(mPasswordField.getText().toString()))
            {
                errorView.setText(R.string.wrong_password_error);
                return false;
            }
        }
        else
        {
            // Checking that the email field is not empty
            if("".equals(mEmailField.getText().toString()))
            {
                errorView.setText(R.string.empty_email_error);
                return false;
            }
            // Checking that the confirm password field is not empty
            if("".equals(mConfirmPasswordField.getText().toString()))
            {
                errorView.setText(R.string.empty_confirm_password_error);
                return false;
            }
            // Checking that the password and confirm password fields match
            if(!mPasswordField.getText().toString().equals(mConfirmPasswordField.getText().toString()))
            {
                errorView.setVisibility(View.VISIBLE);
                errorView.setText(R.string.password_mismatch_error);
                return false;
            }
        }

        errorView.setVisibility(View.GONE);
        return true;
    }
}
