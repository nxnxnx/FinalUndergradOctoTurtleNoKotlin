package io.github.nelvson.finalundergradoctoturtlenokotlin;

import android.content.Intent;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Thread.sleep;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng home = new LatLng(-6.2456559,106.6217354);
    int dist[] = new int[60];
    String namaToko[] = new String[60];

    double Lat[] = new double[60];
    double Lng[] = new double[60];
    int param = 0;

    public RatingBar ratingBar;

    int position, norut;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        norut = intent.getIntExtra("norut",0);
        LatLng X = new LatLng(-6.2456559,106.6217354);
        String pathClosestStore = urlFindClosestConvinience(2500,X);

        String json = passClosestPathJSON(pathClosestStore);
        calculateClosestStore(json);
        String[] myKeys = getResources().getStringArray(R.array.sections);
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

        LatLng sydney = new LatLng(Lat[position], Lng[position]);
        float zoomLvl = 19.0f;

        mMap.addMarker(new MarkerOptions().position(sydney).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLvl));
    }

    public void createMarker(JSONArray results, int idx, GoogleMap googleMap){
        try {
            JSONObject geometry = results.getJSONObject(idx).getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            Double lat = location.getDouble("lat");
            Double lng = location.getDouble("lng");
            LatLng mark = new LatLng(lat, lng);
            GoogleMap newmap = googleMap;
            String storeName = results.getJSONObject(idx).get("name").toString();
            newmap.addMarker(new MarkerOptions().position(mark).title(storeName));
        } catch(JSONException e) {

        }
    }

    public String getStoreDistance(String json){
        String distance_value="";
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
            distance_value = distance.get("value").toString();
            createArrayStoreDistance(distance_value);
        }
        catch (JSONException e){
            logg("no");
        }
        return distance_value;
    }

    public void createArrayStoreDistance(String distance){
        dist[param] = Integer.parseInt(distance);
        logg("newline \n"+ String.valueOf(param) + "\n"+String.valueOf(dist[param]));

        param++;
        if (param==60) param=0;
    }

    public void createArrayStoreName(String name){
        namaToko[param] = name;
 //       logg(namaToko[param]);
    }

    public void submitButton(View view){
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
        double ratedValue = ratingBar.getRating();
        String pathNewRating = "http://104.215.190.135/newRating?id="+norut+"&rating="+ratedValue;
        passClosestPathJSON(pathNewRating);
        Intent backToMainIntent = new Intent();
        backToMainIntent.setClass(this, MainActivity.class);
        startActivity(backToMainIntent);
    }

    public void calculateClosestStore(String json){
        try {

            String pathUpper = "http://104.215.190.135/getUpper";
            String pathLower = "http://104.215.190.135/getLower";
            String jsonUpper = passClosestPathJSON(pathUpper);
            String jsonLower = passClosestPathJSON(pathLower);

            JSONArray jsonArrayLower = new JSONArray(jsonLower);
            JSONArray jsonArrayUpper = new JSONArray(jsonUpper);
            int ArrayLowerLength = jsonArrayLower.length();
            int ArrayUpperLength = jsonArrayUpper.length();

            for (int i=0; ArrayUpperLength>i;i++){
                String norutToko = (jsonArrayUpper.getJSONObject(i).getString("norut_tb_toko"));
                int nomorToko = Integer.parseInt(norutToko);
                double latToko = jsonArrayUpper.getJSONObject(i).getDouble("lat_tb_toko");
                double lngToko = jsonArrayUpper.getJSONObject(i).getDouble("lng_tb_toko");
                Lat[nomorToko] = latToko;
                Lng[nomorToko] = lngToko;
            }

            for (int i=0; ArrayLowerLength>i;i++){
                String norutToko = (jsonArrayLower.getJSONObject(i).getString("norut_tb_toko"));
                int nomorToko = Integer.parseInt(norutToko);
                double latToko = jsonArrayLower.getJSONObject(i).getDouble("lat_tb_toko");
                double lngToko = jsonArrayLower.getJSONObject(i).getDouble("lng_tb_toko");
                Lat[nomorToko] = latToko;
                Lng[nomorToko] = lngToko;
            }
        } catch (JSONException e){
            logg(e.toString());
        }
    }

    public String urlFindMoreClosestConvenience(int radius, LatLng X, String nextPageToken){
        String key = "AIzaSyC8PIs1nyyrmKeCUlyTbTxco9PfrD1TKtc";
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location="+X.latitude+","+X.longitude);
        url.append("&radius="+radius);
        url.append("&type=convenience_store");
        url.append("&key="+key);
        url.append("&pagetoken="+nextPageToken);
        return url.toString();
    }

    public String urlFindClosestConvinience(int radius, LatLng X){
        String key = "AIzaSyC8PIs1nyyrmKeCUlyTbTxco9PfrD1TKtc";
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location="+X.latitude+","+X.longitude);
        url.append("&radius="+radius);
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

    private String urlCalculateClosestPath(LatLng X, String store_place_id){
        String key = "AIzaSyC8PIs1nyyrmKeCUlyTbTxco9PfrD1TKtc";
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin="+X.latitude+","+X.longitude);
        url.append("&destination=place_id:"+store_place_id);
        url.append("&key="+key);
        return url.toString();
    }
}
