package com.example.andiec.visualkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/*
    Splash screen showing the name and the icon of the app for 5 seconds.
 */
public class SplashActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setting the layout of the activity
        setContentView(R.layout.activity_splash);

        // Waiting for 5 seconds before starting the main activity
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        },5000);
    }
}
