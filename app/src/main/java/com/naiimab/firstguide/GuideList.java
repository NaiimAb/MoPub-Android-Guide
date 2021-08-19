package com.naiimab.firstguide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.naiimab.firstguide.JsonReader.AsyncItems;

import java.util.ArrayList;
import java.util.List;

public class GuideList extends AppCompatActivity {

    RecyclerView itemList;
    CustomAdapter customAdapter;

    List<Object> itemsModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_list);

        itemList = findViewById(R.id.itemList);
        itemList.setHasFixedSize(true);
        itemList.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    @SuppressLint("StaticFieldLeak")
    private void getData() {
        new AsyncItems(GuideList.this, CustomUtils.JSON_LINK) {
            @Override
            protected void onDataFirst() {

            }

            @Override
            protected void onDataFinished(List<Object> modelItems, String status) {
                itemsModelList = modelItems;
                if (MyApp.NetworkAds.equalsIgnoreCase("admob")) {
                    loadNatAdmob();
                }
                else if(MyApp.NetworkAds.equalsIgnoreCase("mopub")) {
                    getMoPubAdapter();
                }
                else {
                    getAdapterData();
                }
            }
        }.execute();
    }

    private List<NativeAd> mNativeAds = new ArrayList<>();
    private AdLoader adLoader;
    private void loadNatAdmob() {

        mNativeAds.clear();
        getAdapterData();
        AdLoader.Builder builder = new AdLoader.Builder(getApplicationContext(), MyApp.NativeAdmob);
        adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd NativeAd) {
                mNativeAds.add(NativeAd);
                if (!adLoader.isLoading()) {
                    insertAdsInMenuItems();
                }
            }
        }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        adLoader.loadAds(new AdRequest.Builder().build(), itemsModelList.size());
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        try {
            int offset = (itemsModelList.size() / mNativeAds.size()) + 1;
            int index = 1;
            for (NativeAd  ad : mNativeAds) {
                itemsModelList.add(index, ad);
                index = index + offset;
            }
            customAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d("motya", "Native not replace correct : " + e);
            }
        }
    }


    private void getAdapterData() {
        customAdapter = new CustomAdapter(GuideList.this, itemsModelList);
        itemList.setAdapter(customAdapter);
    }

    private void getMoPubAdapter() {
        customAdapter = new CustomAdapter(GuideList.this, itemsModelList);
        MoPubRecyclerAdapter myMoPubAdapter = new MoPubRecyclerAdapter(GuideList.this, customAdapter);
        // Create an ad renderer and view binder that describe your native ad layout.
        ViewBinder myViewBinder = new ViewBinder.Builder(R.layout.my_ad_layout)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mainImageId(R.id.native_main_image)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build();

        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(myViewBinder);

        myMoPubAdapter.registerAdRenderer(myRenderer);

        // Set up the RecyclerView and start loading ads
        itemList.setAdapter(myMoPubAdapter);
        myMoPubAdapter.loadAds(MyApp.NativeMopub);
    }
}