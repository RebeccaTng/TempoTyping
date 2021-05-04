package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Homescreen extends AppCompatActivity {
    private Button play;
    private Button leaderboards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.play);
        leaderboards = findViewById(R.id.leaderboards);
    }

    public void goPlay(View caller) {
        Intent goToPlay = new Intent(this, Play.class);
        startActivity(goToPlay);
    }

    public void goLeaderboards(View caller) {
        Intent goToLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(goToLeaderboards);
    }
}