package com.naiimab.firstguide.Ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.naiimab.firstguide.CustomUtils;
import com.naiimab.firstguide.MyApp;

public class AdmobAds {

    public interface AdFinished {
        void onAdFinished();
    }

    private final AppCompatActivity mActivity;
    private InterstitialAd mInterstitialAd;


    public AdmobAds(AppCompatActivity activity) {
        this.mActivity = activity;
        MobileAds.initialize(activity);
    }

    public void showBanner(RelativeLayout adContainer) {
        AdView adView = new AdView(mActivity);
        adView.setAdUnitId(MyApp.BannerAdmob);
        adContainer.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);
    }



    public void loadInter() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mActivity, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });

    }

    public void showInter(final AdFinished adFinished) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(mActivity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    adFinished.onAdFinished();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    adFinished.onAdFinished();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    loadInter();
                }
            });

        } else {
            adFinished.onAdFinished();
        }
    }

}
