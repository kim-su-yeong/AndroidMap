package com.example.myapplication;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Naverlocation extends AsyncTask<String, String, String> {
    private OnDownloadCallback myCallback;
    private StringBuilder urlBuilder;
    private URL url;
    private HttpURLConnection conn;
    private MainActivity mMainActivity;

    public interface OnDownloadCallback {
        void onDownlaodedPnu(String pnu);
    }

    public Naverlocation(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        //  strCoord = String.valueOf(latLngs[0].longitude) + "," + String.valueOf(latLngs[0].latitude);
        StringBuilder sb = new StringBuilder();

        urlBuilder = new StringBuilder("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?=query=" + strings[0]); /* URL */
        try {
            url = new URL(urlBuilder.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID","1zikanwl9t");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY","VwEjE7FFhaHgJgAXjPYZvUEqiIPrneGGeb2YJ0Tf");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String jsonStr) {
        super.onPostExecute(jsonStr);
        LatLng pnu = getPnu(jsonStr);
        mMainActivity.markup(pnu);

//        String pnu = getPnu(jsonStr);
//        if(myCallback != null) {
//            if(pnu != null) {
//                myCallback.onDownlaodedPnu(pnu);
//            }
//        }
    }

    private LatLng getPnu(String jsonStr) {
        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObj = (JsonObject) jsonParser.parse(jsonStr);
        JsonArray jsonArray = (JsonArray) jsonObj.get("addresses");
        jsonObj = (JsonObject) jsonArray.get(0);
        String latitude = jsonObj.get("x").getAsString();
        String longitude = jsonObj.get("y").getAsString();

        Double addresslatitude = Double.parseDouble(latitude);
        Double addresslongitude = Double.parseDouble(longitude);

        LatLng pnu = new LatLng(addresslongitude, addresslatitude);

        return pnu;
    }
}
