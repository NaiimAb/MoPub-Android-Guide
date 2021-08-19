package com.naiimab.firstguide.JsonReader;

import android.content.Context;
import android.os.AsyncTask;

import com.naiimab.firstguide.CustomUtils;
import com.naiimab.firstguide.Models.ModelItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AsyncItemsFull extends AsyncTask<String, String, String> {

    protected abstract void onDataFirst();
    protected abstract void onDataFinished(List<ModelItems> modelItems, String status);

    Context context;
    String jsonLink;
    private URL url = null;
    private HttpURLConnection httpURLConnection;
    BufferedReader bufferedReader;
    private ModelItems modelItems;
    private List<ModelItems> modelItemsList = new ArrayList<>();

    public AsyncItemsFull(Context context, String jsonLink) {
        this.context = context;
        this.jsonLink = jsonLink;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        onDataFirst();
    }

    @Override
    protected String doInBackground(String... strings) {
        return buildConnection();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String dataProcess = processData(s);
        onDataFinished(modelItemsList, dataProcess);
    }

    private String processData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject guideDataObj = jsonObject.getJSONObject(CustomUtils.JSON_GUIDE_DATA);
            JSONArray jsonArray = guideDataObj.getJSONArray(CustomUtils.JSON_ITEMS);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);

                String title = itemObj.getString(CustomUtils.JSON_TITLE);
                String image = itemObj.getString(CustomUtils.JSON_IMAGE);
                String color = itemObj.getString(CustomUtils.JSON_COLOR);
                String text_size = itemObj.getString(CustomUtils.JSON_TEXT_SIZE);
                boolean is_link = itemObj.getBoolean(CustomUtils.JSON_IS_LINK);
                String link_title = itemObj.getString(CustomUtils.JSON_LINK_TITLE);
                String set_link = itemObj.getString(CustomUtils.JSON_SET_LINK);
                String image_link = itemObj.getString(CustomUtils.JSON_IMAGE_LINK);
                String text = itemObj.getString(CustomUtils.JSON_TEXT);
                boolean is_native = itemObj.getBoolean(CustomUtils.JSON_IS_NATIVE);
                String background = itemObj.getString(CustomUtils.JSON_BACKGROUND);

                modelItems = new ModelItems(title, image, color, text_size, is_link, link_title, set_link, image_link, text, is_native, background);
                modelItemsList.add(modelItems);
            }
            return "OK";

        } catch (JSONException e) {
            e.printStackTrace();
            return "FAILED";
        }

    }

    private String buildConnection() {
        if(CustomUtils.isNetworkAvailable(context)) {

            try {
                url = new URL(jsonLink);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }

            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("GET");
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();

            }

            try {
                int responseCode = httpURLConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return buffToString(new InputStreamReader(inputStream));
                }
                else {
                    return "Cannot Connect to Server. Please try again";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }

        }

        return "Cannot Connect to Server. Please try again";
    }

    private String buffToString(Reader ourReader) {
        try {
            bufferedReader = new BufferedReader(ourReader);
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            return (result.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }


}
