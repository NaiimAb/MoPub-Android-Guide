package com.naiimab.firstguide;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;

import org.json.JSONException;
import org.json.JSONObject;

public class MyApp extends MultiDexApplication {


    RequestQueue requestQueue;

    public static int isJsonDone = 0; // 0 not yet processed 1 json is done 2 error

    public static String NetworkAds = "admob";
    public static String BannerAdmob = "ca-app-pub-3940256099942544/6300978111";
    public static String InterstitialAdmob = "ca-app-pub-3940256099942544/1033173712";
    public static String NativeAdmob = "ca-app-pub-3940256099942544/2247696110";

    public static String BannerMopub = "b195f8dd8ded45fe847ad89ed1d016da";
    public static String InterstitialMopub = "24534e1901884e398f1253216226017e";
    public static String NativeMopub = "11a17b188668469fb0412708c3d16813";

    public static boolean ImageBanner = false;
    public static String ImageBannerImg = "https://media3.giphy.com/media/igVvRDJ4EbhstQyEKh/giphy.gif";
    public static String ImageBannerURL = "https://www.google.com";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueue = Volley.newRequestQueue(this);


        callAds();
    }

    private void callAds() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, CustomUtils.JSON_LINK, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response)
                    {
                        try {
                            JSONObject GuideData = ((JSONObject) response).getJSONObject("GuideData");
                            JSONObject AdsController = GuideData.getJSONObject("AdsController");

                            NetworkAds = AdsController.getString("NetworkAds");
                            BannerAdmob = AdsController.getString("BannerAdmob");
                            InterstitialAdmob = AdsController.getString("InterstitialAdmob");
                            NativeAdmob = AdsController.getString("NativeAdmob");
                            ImageBanner = AdsController.getBoolean("ImageBanner");
                            ImageBannerImg = AdsController.getString("ImageBannerImg");
                            ImageBannerURL = AdsController.getString("ImageBannerURL");

                            isJsonDone = 1;

                        } catch (JSONException e) {
                            isJsonDone = 2;
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        isJsonDone = 2;
                    }
                });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }
}
