package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Summary extends AppCompatActivity {
    private TextView score;
    private TextView yourPlacement;
    private RequestQueue requestQueue;
    private long wpm;
    private boolean regularGame;
    private String playerName;
    private TextView accuracy;
    private int accuracyPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_summary);

        score = findViewById(R.id.score);
        accuracy = findViewById(R.id.accuracy);
        yourPlacement = findViewById(R.id.placement);

        Bundle extras = getIntent().getExtras();
        regularGame = extras.getBoolean("Gamemode");
        playerName = extras.getString("Player");
        wpm = extras.getLong("WPM");
        score.setText(wpm + " wpm");
        accuracyPercent = extras.getInt("AccuracyPercent");
        accuracy.setText(accuracyPercent + "% accuracy");

        getPlacement();
    }

    public void goLeaderboards(View caller) {
        Intent goToLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(goToLeaderboards);
        overridePendingTransition(0, 0);
    }

    public void goPlay(View caller) {
        Intent goToPlay = new Intent(this, Play.class);
        startActivity(goToPlay);
        overridePendingTransition(0, 0);
    }

    private void getPlacement() {
        requestQueue = Volley.newRequestQueue(this);
        String requestURL;
        if (regularGame) {
            requestURL = "https://studev.groept.be/api/a20sd202/regularPlacement/";
        } else {
            requestURL = "https://studev.groept.be/api/a20sd202/scramblePlacement/";
        }

        String requestPlacementURL = requestURL + wpm + "/" + playerName;
        System.out.println(wpm + playerName +"");

        JsonArrayRequest placementRequest = new JsonArrayRequest(Request.Method.GET, requestPlacementURL, null,
                response -> {
                    try {
                        JSONObject curObject = response.getJSONObject(0);
                        int placement = curObject.getInt("RowNumber");
                        yourPlacement.setText("#" + placement);
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },

                error -> yourPlacement.setText(error.getLocalizedMessage())
        );
        requestQueue.add(placementRequest);
    }
}