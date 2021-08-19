package com.naiimab.firstguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.naiimab.firstguide.JsonReader.AsyncItems;
import com.naiimab.firstguide.JsonReader.AsyncItemsFull;
import com.naiimab.firstguide.Models.ModelItems;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Details extends AppCompatActivity {

    RelativeLayout detailsRelative;
    TextView titleTV, textTV;
    ImageView imageIV;
    Button linkBTN;
    int position = 0;

    LinearLayout nativeLayout;
    TemplateView templateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsRelative = findViewById(R.id.detailsRelative);
        titleTV = findViewById(R.id.titleTV);
        textTV = findViewById(R.id.textTV);
        imageIV = findViewById(R.id.imageIV);
        linkBTN = findViewById(R.id.linkBTN);
        nativeLayout = findViewById(R.id.nativeLayout);
        templateView = findViewById(R.id.templateView);

        position = getIntent().getIntExtra("position", 0);


        if(MyApp.NetworkAds.equalsIgnoreCase("admob")) {
            templateView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(Details.this, MyApp.NativeAdmob)
                    .forNativeAd(unifiedNativeAd -> templateView.setNativeAd(unifiedNativeAd))
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
        else {
            templateView.setVisibility(View.GONE);
            AdapterHelper adapterHelper = new AdapterHelper(this, 0, 3); // When standalone, any range will be fine.
            MoPubNative moPubNative = new MoPubNative(this, MyApp.NativeMopub, new MoPubNative.MoPubNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeAd nativeAd) {
                    // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                    View v = adapterHelper.getAdView(null, ((LinearLayout) findViewById(R.id.nativeLayout)), nativeAd, new ViewBinder.Builder(0).build());
                    // Set the native event listeners (onImpression, and onClick).
                    // Add the ad view to our view hierarchy
                    ((LinearLayout) findViewById(R.id.nativeLayout)).addView(v);
                }

                @Override
                public void onNativeFail(NativeErrorCode nativeErrorCode) {

                }
            });

            ViewBinder myViewBinder = new ViewBinder.Builder(R.layout.my_ad_layout)
                    .titleId(R.id.native_title)
                    .textId(R.id.native_text)
                    .mainImageId(R.id.native_main_image)
                    .iconImageId(R.id.native_icon_image)
                    .callToActionId(R.id.native_cta)
                    .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                    .build();

            MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(myViewBinder);
            moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);

            EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                    RequestParameters.NativeAdAsset.TITLE,
                    RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                    RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.ICON_IMAGE,
                    RequestParameters.NativeAdAsset.STAR_RATING
            );

            RequestParameters mRequestParameters = new RequestParameters.Builder()
                    .desiredAssets(desiredAssets)
                    .build();

            moPubNative.makeRequest(mRequestParameters);
        }


        getData();
    }


    @SuppressLint("StaticFieldLeak")
    private void getData() {
        new AsyncItemsFull(Details.this, CustomUtils.JSON_LINK) {
            @Override
            protected void onDataFirst() {

            }

            @Override
            protected void onDataFinished(List<ModelItems> modelItems, String status) {
                    final ModelItems modelItems1 = modelItems.get(position);

                titleTV.setText(modelItems1.getTitle());

                Glide.with(Details.this).load(modelItems1.getImage()).error(R.mipmap.ic_launcher).into(imageIV);

                imageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!modelItems1.getImage_link().isEmpty()) {
                            String url = modelItems1.getImage_link();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }
                });

                textTV.setText(modelItems1.getText());

                if(!modelItems1.getColor().isEmpty()) {
                    titleTV.setTextColor(Color.parseColor(modelItems1.getColor()));
                    textTV.setTextColor(Color.parseColor(modelItems1.getColor()));
                }

                if(!modelItems1.getText_size().isEmpty()) {
                    textTV.setTextSize(Integer.parseInt(modelItems1.getText_size()));
                }

                if(modelItems1.getIsLink()) {
                    linkBTN.setVisibility(View.VISIBLE);
                    if(!modelItems1.getLink_title().isEmpty()) {
                        linkBTN.setText(modelItems1.getLink_title());
                    }

                    linkBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!modelItems1.getSetLink().isEmpty()) {
                                String url = modelItems1.getSetLink();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }
                    });
                }
                else {
                    linkBTN.setVisibility(View.GONE);
                }

                if(!modelItems1.getBackground().isEmpty()) {
                    detailsRelative.setBackgroundColor(Color.parseColor(modelItems1.getBackground()));
                }

            }
        }.execute();
    }
}