package com.naiimab.firstguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MyApp.isJsonDone == 0) {
                    handler.postDelayed(this, CustomUtils.TIME);
                }
                else if(MyApp.isJsonDone == 1) {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else if(MyApp.isJsonDone == 2) {
                    Toast.makeText(SplashScreen.this, "Error. Please try again later", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, CustomUtils.TIME);

    }
}