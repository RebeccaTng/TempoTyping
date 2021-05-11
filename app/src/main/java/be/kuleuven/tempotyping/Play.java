package be.kuleuven.tempotyping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Play extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);

    }

    public void goGamemode(View caller) {
        boolean regularGame;
        switch(caller.getId())
        {
            case R.id.regular:
                regularGame = true;
                break;
            case R.id.keyboardSmash:
                regularGame = false;
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }
        Intent goToGamemode = new Intent(this, Gamemode.class);
        goToGamemode.putExtra("Gamemode", regularGame);
        startActivity(goToGamemode);
    }

    public void goHomescreen(View caller) {
        Intent goToHomescreen = new Intent(this, Homescreen.class);
        startActivity(goToHomescreen);
    }
}