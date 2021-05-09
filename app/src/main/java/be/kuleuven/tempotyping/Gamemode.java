package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Gamemode extends AppCompatActivity {
    private TextView timer;
    private TextView toType;
    private RequestQueue requestText;
    private static boolean regularGame;
    private Button summary;
    private EditText typeHere;
    private long diff;

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
        summary = findViewById(R.id.summary);
        typeHere = findViewById(R.id.typeHere);

        long duration = 4000;
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                typeHere.setVisibility(View.VISIBLE);
                upwardCounter();
            }
        }.start();

        requestText();
    }

    public void upwardCounter()
    {
        long maxCounter = 5000;
        new CountDownTimer(maxCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                diff = maxCounter - millisUntilFinished;
                String upDuration = String.format(Locale.ENGLISH,"%02d:%02d"
                        ,TimeUnit.MILLISECONDS.toMinutes(diff)
                        ,TimeUnit.MILLISECONDS.toSeconds(diff)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
                timer.setText(upDuration);
            }

            public void onFinish() {
                timer.setText("Time's up!");
                typeHere.setVisibility(View.INVISIBLE);
                summary.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void requestText() {
        requestText = Volley.newRequestQueue(this);
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
        requestText.add(textRequest);
    }

    public void goSummary(View caller) {
        Intent goToSummary = new Intent(this, Summary.class);
        goToSummary.putExtra("Time", diff); //diff value niet exact genoeg, TODO: extract value from upDuration
        startActivity(goToSummary);
    }
}