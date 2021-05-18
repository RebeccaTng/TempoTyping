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

public class Leaderboards extends AppCompatActivity {
    private TextView regular_scores;
    private TextView scramble_scores;
    private RequestQueue requestScramble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leaderboards);

        regular_scores = findViewById(R.id.regular_scores);
        scramble_scores = findViewById(R.id.scramble_scores);

        requestRegularLB();
        requestScrambleLB();
    }

    public void goHomescreen(View caller) {
        Intent goToHomescreen = new Intent(this, Homescreen.class);
        startActivity(goToHomescreen);
        overridePendingTransition(0, 0);
    }

    private void requestRegularLB() {
        requestScramble = Volley.newRequestQueue(this);
        String requestRegularURL = "https://studev.groept.be/api/a20sd202/regularLeaderboard";

        JsonArrayRequest regularLBRequest = new JsonArrayRequest(Request.Method.GET, requestRegularURL, null,

                response -> {
                    try {
                        String responseString = "";
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject curObject = response.getJSONObject(i);
                            int place = i + 1;
                            responseString += place + "." + "\t" + "\t" + "\t" + curObject.getInt("wpm") + "\t" + "\t" + "\t" + curObject.getString("player") + "\n";
                        }
                        regular_scores.setText(responseString);
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },

                error -> regular_scores.setText(error.getLocalizedMessage())
        );
        requestScramble.add(regularLBRequest);
    }

    private void requestScrambleLB() {
        requestScramble = Volley.newRequestQueue(this);
        String requestRegularURL = "https://studev.groept.be/api/a20sd202/scrambleLeaderboard";

        JsonArrayRequest scrambleLBRequest = new JsonArrayRequest(Request.Method.GET, requestRegularURL, null,

                response -> {
                    try {
                        String responseString = "";
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject curObject = response.getJSONObject(i);
                            int place = i + 1;
                            responseString += place + "." + "\t" + "\t" + "\t" + curObject.getInt("wpm") + "\t" + "\t" + "\t" + curObject.getString("player") + "\n";
                        }
                        scramble_scores.setText(responseString);
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },

                error -> scramble_scores.setText(error.getLocalizedMessage())
        );
        requestScramble.add(scrambleLBRequest);
    }
}