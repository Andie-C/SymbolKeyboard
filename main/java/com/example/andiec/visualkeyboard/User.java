package com.example.andiec.visualkeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/*
    User credentials.
 */
public class User
{
    private static User sUser;

    private String mUsername;
    private String mEmail;
    private String mPassword;
    private boolean mLoggedIn;

    public static User get(Context context)
    {
        if(sUser == null)
            sUser = new User(context);
        return sUser;
    }

    /*
        Retrieves user credentials from app's preferences.
     */
    private User(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mUsername = sharedPref.getString(context.getString(R.string.username), "");
        mEmail = sharedPref.getString(context.getString(R.string.email), "");
        mPassword = sharedPref.getString(context.getString(R.string.password), "");
        mLoggedIn = sharedPref.getBoolean(context.getString(R.string.logged_in), false);
    }

    /*
        Updates the user's username in the app's preferences.
     */
    public void setUsername(Context context, String username)
    {
        mUsername = username;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.username), username);
        editor.apply();
    }

    /*
        Updates the user's email in the app's preferences.
     */
    public void setEmail(Context context, String email)
    {
        mEmail = email;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.email), email);
        editor.apply();
    }

    /*
        Updates the user's password in the app's preferences.
     */
    public void setPassword(Context context, String password)
    {
        mPassword = password;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.password), password);
        editor.apply();
    }

    /*
        Retrieves the user's username.
     */
    public String getUsername()
    {
        return mUsername;
    }

    /*
        Retrieves the user's email.
     */
    public String getEmail()
    {
        return mEmail;
    }

    /*
        Compares the user's password to the given one.
     */
    public boolean checkPassword(String password)
    {
        return mPassword.equals(password);
    }

    /*
        Updates the user's status in the app's preferences.
     */
    public void logIn(Context context)
    {
        mLoggedIn = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.logged_in), true);
        editor.apply();
    }

    /*
        Updates the user's status in the app's preferences.
     */
    public void logOut(Context context)
    {
        mLoggedIn = false;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.logged_in), false);
        editor.apply();
    }

    /*
        Retrieves the user's status.
     */
    public boolean isLoggedIn()
    {
        return mLoggedIn;
    }
}
