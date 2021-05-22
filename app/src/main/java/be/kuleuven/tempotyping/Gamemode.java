package be.kuleuven.tempotyping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Gamemode extends AppCompatActivity {
    private TextView timer;
    private TextView toType;
    private TextView currentWord;
    private RequestQueue requestQueue;
    private boolean regularGame;
    private EditText typeHere;
    private long difference;
    private String playerName;
    private long wpm;
    private int textIndex;
    private String[] splitWords;
    private int mistakes;
    private long id;

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
        currentWord = findViewById(R.id.currentWord);

        typeHere.getBackground().setColorFilter(null);
        typeHere.setTextColor(Color.BLACK);

        requestText();
        getID();
        downwardCounter();
    }

    public void startGame()
    {
        String toTypeText = toType.getText().toString();
        splitWords = toTypeText.split(" ");
        textIndex = 0;
        mistakes = 0;
        currentWord.setText(splitWords[0]);

        typeHere.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    if (typeHere.getText().toString().equals(splitWords[textIndex])) {
                        typeHere.getBackground().setColorFilter(null);
                        typeHere.setTextColor(Color.BLACK);
                        typeHere.setText("");
                        textIndex++;
                        if (textIndex < splitWords.length) {
                            currentWord.setText(splitWords[textIndex]);
                        }
                    }else{
                        typeHere.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        typeHere.setTextColor(Color.RED);
                        mistakes++;
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
                typeHere.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(typeHere, InputMethodManager.SHOW_IMPLICIT);
            }
        }.start();
    }

    public void upwardCounter()
    {
        long maxCounter = 180000;
        new CountDownTimer(maxCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                difference = maxCounter - millisUntilFinished;
                String upDuration = String.format(Locale.ENGLISH,"%02d:%02d"
                        ,TimeUnit.MILLISECONDS.toMinutes(difference)
                        ,TimeUnit.MILLISECONDS.toSeconds(difference)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
                timer.setText(upDuration);
                if (textIndex>=splitWords.length)
                {
                    onFinish();
                    cancel();
                }
            }

            public void onFinish() {
                timer.setText("Finished!");
                typeHere.setEnabled(false);
                if(!isFinishing())
                {
                    textDialog();
                }
            }
        }.start();
    }

    private void requestText() {
        requestQueue = Volley.newRequestQueue(this);
        String requestRegularURL = "https://studev.groept.be/api/a20sd202/randomText" + gamemode();

        JsonArrayRequest textRequest = new JsonArrayRequest(Request.Method.GET, requestRegularURL, null,
                response -> {
                    try {
                        StringBuilder responseString = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject curObject = response.getJSONObject(i);
                            responseString.append(curObject.getString("text" + gamemode()));
                        }
                        toType.setText(responseString.toString());
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },
                error -> toType.setText(error.getLocalizedMessage())
        );
        requestQueue.add(textRequest);
    }

    public void textDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type your name (only letters)");
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(7);
        input.setFilters(FilterArray);
        builder.setView(input);

        builder.setPositiveButton("GO TO SUMMARY", (dialog, which) -> {
            playerName = input.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
            if (playerName.equals("")) {
                playerName = "guest";
            }

            calculateWPM();
            int accuracyPercent = calculateAccuracy();
            submitScore();

            Intent goToSummary = new Intent(Gamemode.this, Summary.class);
            goToSummary.putExtra("WPM", wpm);
            goToSummary.putExtra("Gamemode", regularGame);
            goToSummary.putExtra("ID", id);
            goToSummary.putExtra("AccuracyPercent", accuracyPercent);
            startActivity(goToSummary);
            overridePendingTransition(0, 0);
        });
        builder.show();
    }

    private int calculateAccuracy() {
        int accuracyPercent = 0;
        if(mistakes+textIndex != 0) {
            accuracyPercent = 100-(100*mistakes/(mistakes+textIndex));
        }
        return accuracyPercent;
    }

    private void calculateWPM() {
        difference = (difference / 1000) + 1;
        wpm = textIndex*60 / difference;
    }

    public void submitScore() {
        String submitScoreURL = "https://studev.groept.be/api/a20sd202/submitScore" + gamemode();
        String submitURL = submitScoreURL + "/" + wpm + "/" + playerName;

        StringRequest submitScore = new StringRequest(Request.Method.GET, submitURL,
                response -> {},
                error -> toType.setText(error.getLocalizedMessage())
        );
        requestQueue.add(submitScore);
    }

    public void getID() {
        String getIdUrl = "https://studev.groept.be/api/a20sd202/getId" + gamemode();

        JsonArrayRequest getID = new JsonArrayRequest(Request.Method.GET, getIdUrl, null,
                response -> {
                    try {
                        JSONObject curObject = response.getJSONObject(0);
                        id = curObject.getLong("id") + 1;
                    } catch (JSONException e) {
                        Log.e("Database", e.getMessage(), e);
                    }
                },
                error -> toType.setText(error.getLocalizedMessage())
        );
        requestQueue.add(getID);
    }

    private String gamemode() {
        if (regularGame) {
            return "Regular";
        } else {
            return "Scramble";
        }
    }
}