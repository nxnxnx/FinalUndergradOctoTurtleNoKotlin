package io.github.nelvson.finalundergradoctoturtlenokotlin;

import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.*;

import org.apache.http.conn.scheme.HostNameResolver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PROXIMITY_RADIUS = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-6.2456559,106.6217354);
        float zoomLvl = 19.0f;

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLvl));
        LatLng X = new LatLng(-6.2456559,106.6217354);
        LatLng Y = new LatLng(-6.2249246, 106.5925628);
        String path = urlCalculateClosestPath(X, Y);
        String json = passClosestPathJSON(path);
        logg(urlFindClosestConvinience(500,X));/*
     //   logg(json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            Gson gson = new GsonBuilder().create();
            JsonObject root = gson.fromJson(json, JsonObject.class);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
            JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
            logg("Distance");
            logg(distance.get("text").toString());
            logg("in meters");
            int asd = Integer.parseInt(distance.get("value").toString());
            logg(distance.get("value").toString());
        } catch (JSONException e){
            logg(e.toString());
        }*/
    }

    public String urlFindClosestConvinience(int radius, LatLng X){
        String key = "AIzaSyC8PIs1nyyrmKeCUlyTbTxco9PfrD1TKtc";
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/json?");
        url.append("location="+X.latitude+","+X.longitude);
        url.append("&radius"+radius);
        url.append("&type=convenience_store");
        url.append("&key="+key);
        return url.toString();
    }


   public String passClosestPathJSON(String reqUrl) {
 //     String response = null;
      StringBuilder result = new StringBuilder();
      try {
         URL url = new URL(reqUrl);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         // read the response
         InputStream in = new BufferedInputStream(conn.getInputStream());
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         String line;
         while ((line = reader.readLine()) != null){
             result.append(line);
         }
      } catch (Exception e) {
          Log.e("yee", "msg", e);
 //         Log.e("yee","failed my dude");
      } finally {
          //ye
      }

      return result.toString();
   }

   public void logg(String message){
        Log.d("yee", message);
   }

   private String urlCalculateClosestPath(LatLng X, LatLng Y){
        String key = "AIzaSyC8PIs1nyyrmKeCUlyTbTxco9PfrD1TKtc";
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin="+X.latitude+","+X.longitude);
        url.append("&destination="+Y.latitude+","+Y.longitude);
        url.append("&key="+key);
        return url.toString();
    }

}
