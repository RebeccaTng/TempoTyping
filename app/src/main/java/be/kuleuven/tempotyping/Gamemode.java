package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Gamemode extends AppCompatActivity {
    private TextView timer;
    private TextView toType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gamemode);

        timer = findViewById(R.id.timer);
        toType = findViewById(R.id.toType);

        long duration = TimeUnit.SECONDS.toMillis(10);
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String sDuration = String.format(Locale.ENGLISH,"%02d:%02d"
                ,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                ,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                timer.setText(sDuration);
            }

            @Override
            public void onFinish() {
                String timeUp = "Time's up!";
                timer.setText(timeUp);
            }
        }.start();
    }

    public void goSummary(View caller) {
        Intent goToSummary = new Intent(this, Summary.class);
        startActivity(goToSummary);
    }
}