package be.kuleuven.tempotyping.activities;

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

import be.kuleuven.tempotyping.R;

public class Leaderboards extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leaderboards);

        TextView regular_scores = findViewById(R.id.regular_scores);
        TextView scramble_scores = findViewById(R.id.scramble_scores);

        requestLB("Regular", regular_scores);
        requestLB("Scramble", scramble_scores);
    }

    public void goHomescreen(View caller) {
        Intent goToHomescreen = new Intent(this, Homescreen.class);
        startActivity(goToHomescreen);
        overridePendingTransition(0, 0);
    }

    private void requestLB(String gamemode, TextView mode) {
        RequestQueue requestLB = Volley.newRequestQueue(this);
        String requestRegularURL = "https://studev.groept.be/api/a20sd202/leaderboard" + gamemode;

        JsonArrayRequest regularLBRequest = new JsonArrayRequest(Request.Method.GET, requestRegularURL, null,
                response -> {
                    try {
                        StringBuilder responseString = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject curObject = response.getJSONObject(i);
                            int place = i + 1;
                            responseString.append(place).append(".").append(space(place))
                                    .append(curObject.getInt("wpm")).append(space(curObject.getInt("wpm")))
                                    .append(curObject.getString("player")).append("\n");
                        }
                        mode.setText(responseString.toString());
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },
                error -> mode.setText(error.getLocalizedMessage())
        );
        requestLB.add(regularLBRequest);
    }

    private StringBuilder space(int place) {
        StringBuilder space = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            space.append("\t");
        }

        if (place < 10) {
            space.append("\t");
        }
        return space;
    }
}