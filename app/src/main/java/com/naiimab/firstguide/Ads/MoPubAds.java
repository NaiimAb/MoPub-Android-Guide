package com.naiimab.firstguide.Ads;

import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.naiimab.firstguide.MyApp;

public class MoPubAds {

    public interface AdFinished {
        void onAdFinished();
    }

    private final AppCompatActivity mActivity;



    public MoPubAds(AppCompatActivity activity) {
        this.mActivity = activity;

        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(MyApp.BannerMopub)
                .build();

        MoPub.initializeSdk(mActivity, sdkConfiguration, () -> { });
    }

    public void showBanner(RelativeLayout adContainer) {
        MoPubView moPubView = new MoPubView(mActivity);
        moPubView.setAdUnitId(MyApp.BannerMopub);
        moPubView.setAdSize(MoPubView.MoPubAdSize.HEIGHT_90);
        adContainer.addView(moPubView);
        moPubView.loadAd();
    }

    public void showInter(final AdFinished adFinished) {
        MoPubInterstitial mInterstitial = new MoPubInterstitial(mActivity, MyApp.InterstitialMopub);
        mInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
                mInterstitial.show();
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
                adFinished.onAdFinished();
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
                adFinished.onAdFinished();
            }
        });
        mInterstitial.load();
    }

}
