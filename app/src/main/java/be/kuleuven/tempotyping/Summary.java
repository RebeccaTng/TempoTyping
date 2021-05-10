package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class Summary extends AppCompatActivity {
    private TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_summary);

        score = findViewById(R.id.score);

        Bundle extras = getIntent().getExtras();
        long wpm = 50*60000/extras.getLong("Time"); // 50 = placeholder voor aantal woorden in tekst
        String scoreString = wpm + " wpm";
        score.setText(scoreString);

    }

    public void goLeaderboards(View caller) {
        Intent goToLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(goToLeaderboards);
    }

    public void goPlay(View caller) {
        Intent goToPlay = new Intent(this, Play.class);
        startActivity(goToPlay);
    }
}