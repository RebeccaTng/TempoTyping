package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class Play extends AppCompatActivity {
    private Button regular;
    private Button keyboardSmash;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);

        regular = findViewById(R.id.regular);
        keyboardSmash = findViewById(R.id.keyboardSmash);
        back = findViewById(R.id.back);
    }

    public void goGamemode(View caller) {
        switch(caller.getId())
        {
            case R.id.regular:
                Gamemode.setRegularGame(true);
                break;
            case R.id.keyboardSmash:
                Gamemode.setRegularGame(false);
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }

        Intent goToGamemode = new Intent(this, Gamemode.class);
        startActivity(goToGamemode);
    }

    public void goHomescreen(View caller) {
        Intent goToHomescreen = new Intent(this, Homescreen.class);
        startActivity(goToHomescreen);
    }
}