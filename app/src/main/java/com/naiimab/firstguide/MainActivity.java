package com.naiimab.firstguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.naiimab.firstguide.Ads.AdmobAds;
import com.naiimab.firstguide.Ads.CPAAds;
import com.naiimab.firstguide.Ads.MoPubAds;

public class MainActivity extends AppCompatActivity {

    Button letStart;
    AdmobAds admobAds;
    CPAAds cpaAds;
    MoPubAds moPubAds;
    RelativeLayout adLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        letStart = findViewById(R.id.letStart);
        adLayout = findViewById(R.id.adLayout);


        admobAds = new AdmobAds(this);
        cpaAds = new CPAAds(this);
        moPubAds = new MoPubAds(this);

        admobAds.loadInter();

        if(MyApp.ImageBanner) {
            cpaAds.showBanner(adLayout, CustomUtils.getScreenSize(MainActivity.this, true), CustomUtils.getScreenSize(MainActivity.this, true)/2);
        }
        else {
            if (MyApp.NetworkAds.equalsIgnoreCase("admob")) {
                admobAds.showBanner(adLayout);
            }
            else {
                moPubAds.showBanner(adLayout);
            }
        }
        letStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyApp.NetworkAds.equalsIgnoreCase("admob")) {
                    admobAds.showInter(new AdmobAds.AdFinished() {
                        @Override
                        public void onAdFinished() {
                            Intent goIntent = new Intent(MainActivity.this, SecActivity.class);
                            startActivity(goIntent);
                        }
                    });
                }
                else {
                    moPubAds.showInter(new MoPubAds.AdFinished() {
                        @Override
                        public void onAdFinished() {
                            Intent goIntent = new Intent(MainActivity.this, SecActivity.class);
                            startActivity(goIntent);
                        }
                    });
                }

            }
        });

    }
}