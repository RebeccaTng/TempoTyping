package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.graphics.PorterDuff.Mode.ADD;

public class Gamemode extends AppCompatActivity {
    private TextView timer;
    private TextView toType;
    private RequestQueue requestQueue;
    private boolean regularGame;
    private EditText typeHere;
    private long diff;
    private String playerName;
    private long wpm;
    private int textIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gamemode);

        Bundle extras = getIntent().getExtras();
        regularGame = extras.getBoolean("Gamemode");

        timer = findViewById(R.id.timer);
        toType = findViewById(R.id.toType);
        typeHere = findViewById(R.id.typeHere);

        requestText();
        downwardCounter();
    }

    public void startGame()
    {
        String toTypeText = toType.getText().toString();
        String[] splitWords = toTypeText.split(" ");
        textIndex = 0;

        typeHere.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    if (typeHere.getText().toString().equals(splitWords[textIndex])) {
                        typeHere.setBackgroundTintMode(null);
                        typeHere.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                        //typeHere.setBackgroundResource(R.drawable.type_here_back);
                        typeHere.setText("");
                        textIndex++;
                    }else{
                        typeHere.setBackgroundTintMode(ADD);
                        typeHere.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#92ED0D0D")));
                        //typeHere.setBackgroundResource(R.drawable.type_here_back);
                    }
                }
                return handled;
            }
        });
    }

    public void downwardCounter()
    {
        long duration = 6000;
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                typeHere.setHint("Type here");
                typeHere.setEnabled(true);
                startGame();
                upwardCounter();
            }
        }.start();
    }

    public void upwardCounter()
    {
        long maxCounter = 10000;
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
                typeHere.setEnabled(false);
                textDialog();
            }
        }.start();
    }

    private void requestText() {
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

    private void textDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type your name (only letters)");
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(6);
        input.setFilters(FilterArray);
        builder.setView(input);

        builder.setPositiveButton("GO TO SUMMARY", (dialog, which) -> {
            playerName = input.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
            if (playerName.equals("")) {
                playerName = "guest";
            }
            // 50 = placeholder voor aantal woorden in tekst
            wpm = 50*60000/diff; //diff value niet exact genoeg, TODO: extract value from upDuration
            submitScore();
            Intent goToSummary = new Intent(Gamemode.this, Summary.class);
            goToSummary.putExtra("WPM", wpm);
            goToSummary.putExtra("Gamemode", regularGame);
            goToSummary.putExtra("Player", playerName);
            startActivity(goToSummary);
            overridePendingTransition(0, 0);
        });
        builder.show();
    }

    private void submitScore() {
        String submitScoreURL;
        if (regularGame) {
            submitScoreURL = "https://studev.groept.be/api/a20sd202/submitRegularScore/";
        } else {
            submitScoreURL = "https://studev.groept.be/api/a20sd202/submitScrambleScore/";
        }

        String submitURL = submitScoreURL + wpm + "/" + playerName;

        StringRequest submitScore = new StringRequest(Request.Method.GET, submitURL,
                response -> {},
                error -> toType.setText(error.getLocalizedMessage())
        );
        requestQueue.add(submitScore);
    }
}