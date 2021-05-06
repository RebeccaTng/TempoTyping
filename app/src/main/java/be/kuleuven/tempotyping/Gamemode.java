package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Gamemode extends AppCompatActivity {
    private TextView timer;
    private TextView toType;
    private RequestQueue requestQueue;
    private static boolean regularGame;

    public static void setRegularGame(boolean regularGame) {
        Gamemode.regularGame = regularGame;
    }

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

        requestQueue = Volley.newRequestQueue(this);
        String requestRegularURL;
        if (regularGame) {
            requestRegularURL = "https://studev.groept.be/api/a20sd202/randomRegularText";
        } else {
            requestRegularURL = "https://studev.groept.be/api/a20sd202/randomScrambleText";
        }

        JsonArrayRequest textRequest = new JsonArrayRequest(Request.Method.GET, requestRegularURL, null,

                response -> {
                    try {
                        String responseString = "";
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject curObject = response.getJSONObject(i);
                            if (regularGame) {
                                responseString += curObject.getString("regularText");
                            } else {
                                responseString += curObject.getString("scrambleText");
                            }
                        }
                        toType.setText(responseString);
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },

                error -> toType.setText(error.getLocalizedMessage())
        );
        requestQueue.add(textRequest);
    }

    public void goSummary(View caller) {
        Intent goToSummary = new Intent(this, Summary.class);
        startActivity(goToSummary);
    }
}