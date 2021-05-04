package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Homescreen extends AppCompatActivity {
    private Button play;
    private Button leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (Button) findViewById(R.id.play);
        leaderboard = (Button) findViewById(R.id.leaderboard);
    }

    public void goPlay(View caller) {
        Intent goToPlay = new Intent(this, Play.class);
        startActivity(goToPlay);
    }
}