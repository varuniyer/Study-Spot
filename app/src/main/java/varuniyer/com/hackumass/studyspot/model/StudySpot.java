/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package varuniyer.com.hackumass.studyspot.model;

import varuniyer.com.hackumass.studyspot.ui.*;
import varuniyer.com.hackumass.studyspot.model.HighlightedResult;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A StudySpot object from the data model.
 */
public class StudySpot
{
    public String name;
    public String volume;
    public String solo;
    public String group;
    public String sca;
    public String outlets;
    public String charging;
    public String whiteboard;
    public String printer;
    public double dist;
    public double lat;
    public double lon;
    public Location loc;

    public StudySpot(final Context context, String name, String volume, String solo, String group, String sca,
                     String outlets, String charging, String whiteboard, String printer,
                     double dist, double lat, double lon)
    {
        this.name = name;
        this.volume = volume;
        this.solo = solo;
        this.group = group;
        this.sca = sca;
        this.outlets = outlets;
        this.charging = charging;
        this.whiteboard = whiteboard;
        this.printer = printer;
        this.lat = lat;
        this.lon = lon;

        new Thread() {
            public void run() {
                try {
                    LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null)
                        setDist(getDistanceInfo(loc.getLatitude(), loc.getLongitude()));
                    LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    List<String> providers = mLocationManager.getProviders(true);
                    for (String provider : providers) {
                        Location l = mLocationManager.getLastKnownLocation(provider);
                        if (l == null) {
                            continue;
                        }
                        if (loc == null || l.getAccuracy() < loc.getAccuracy()) {
                            // Found best last known location: %s", l);
                            loc = l;
                            Log.i("Coordinates", loc.getLatitude() + " " + loc.getLongitude());
                            setDist(getDistanceInfo(loc.getLatitude(), loc.getLongitude()));
                        }
                    }
                } catch (SecurityException s) {
                    Log.i("Security Exception", s.toString());
                }
            }
        }.start();
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    private double getDistanceInfo(double myLat, double myLon) {
        StringBuilder stringBuilder = new StringBuilder();
        double dist = -1.0;
        try {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                    myLat + "," + myLon + "&destination=" + lat + "," + lon +
                    "&mode=walking&key=AIzaSyDakHVdOTOT00sEJ06bqopeqL02SLaO2QM";

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            Log.i("Distance", distance.toString());
            Log.i("Coordinates", myLat + " " + myLon + " " + lat + " " + lon);
            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dist;
    }
}
