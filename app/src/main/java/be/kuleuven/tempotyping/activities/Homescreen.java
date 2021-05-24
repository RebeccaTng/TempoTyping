package be.kuleuven.tempotyping.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import be.kuleuven.tempotyping.R;

public class Homescreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void goPlay(View caller) {
        Intent goToPlay = new Intent(this, Play.class);
        startActivity(goToPlay);
        overridePendingTransition(0, 0);
    }

    public void goLeaderboard(View caller) {
        Intent goToLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(goToLeaderboards);
        overridePendingTransition(0, 0);
    }
}