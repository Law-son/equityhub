package com.equityhub.application.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.equityhub.application.MainActivity;
import com.equityhub.application.R;

public class SplashActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "WebPagePrefs";
    public static final String KEY_VISITED = "visited";
    public static final String DEFAULT_URL = "https://mobile.anyview1.com";

    private Handler handler;
    private Runnable runnable;
    private long remainingTime = 2000; // Timer duration in milliseconds
    private boolean isPaused = false; // Flag to track if the timer is paused

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user has visited the page before
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isVisited = sharedPreferences.getBoolean(KEY_VISITED, false);

        if (isVisited) {
            // If visited, navigate directly to the WebPage activity
            Intent intent = new Intent(SplashActivity.this, WebPage.class);
            startActivity(intent);
            finish();
            return; // Exit the method to skip initializing the splash screen
        }

        // Set the content view to the splash screen layout
        setContentView(R.layout.activity_splash);

        // Initialize Handler
        handler = new Handler();

        // Define the Runnable
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    navigateToMainActivity();
                }
            }
        };

        // Start the timer
        handler.postDelayed(runnable, remainingTime);

        // Find the Privacy Policy TextView
        TextView privacyPolicy = findViewById(R.id.privacy_policy);

        // Set an onClickListener to navigate to the Privacy Policy activity
        privacyPolicy.setOnClickListener(v -> {
            isPaused = true; // Pause the timer
            if (handler != null) {
                handler.removeCallbacks(runnable); // Stop the handler from executing
            }

            Intent intent = new Intent(SplashActivity.this, PrivacyPolicy.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the user comes back from the Privacy Policy activity, resume the timer
        if (isPaused) {
            isPaused = false; // Reset the paused flag
            if (handler != null) {
                handler.postDelayed(runnable, remainingTime); // Resume the timer
            }
        }
    }

    private void navigateToMainActivity() {
        // Navigate to MainActivity
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable); // Prevent memory leaks
        }
    }
}
