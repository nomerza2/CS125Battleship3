package com.example.battleship3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import java.util.concurrent.TimeUnit;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, FullscreenActivity.class);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        startActivity(intent);
        finish();
    }


}