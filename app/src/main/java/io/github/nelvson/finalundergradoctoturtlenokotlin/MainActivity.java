package io.github.nelvson.finalundergradoctoturtlenokotlin;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

       @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        String[] elements = {"Line 1", "Line 2"};
        List<String> elem = new ArrayList<String>();
        //* *EDIT* *
        ListView listview = (ListView) findViewById(R.id.listView1);
        String pathUpper = "http://104.215.190.135/getUpper";
        String pathLower = "http://104.215.190.135/getLower";
        String jsonUpper = passClosestPathJSON(pathUpper);
        String jsonLower = passClosestPathJSON(pathLower);
        try {
            JSONArray jsonArrayLower = new JSONArray(jsonLower);
            JSONArray jsonArrayUpper = new JSONArray(jsonUpper);
            int ArrayLowerLength = jsonArrayLower.length();
            int ArrayUpperLength = jsonArrayUpper.length();

            for (int i=0; ArrayUpperLength>i;i++){
                String pushToko="";
                String norutToko = (jsonArrayUpper.getJSONObject(i).getString("norut_tb_toko"));
                String ratToko = (jsonArrayUpper.getJSONObject(i).getString("rat_tb_toko"));
                String distToko = (jsonArrayUpper.getJSONObject(i).getString("dist_tb_toko"));
                pushToko = "Toko "+norutToko+", rating : "+ratToko+", distance (in meters) : "+distToko;
                elem.add(pushToko);
            }

            for (int i=0; ArrayLowerLength>i;i++){
                String pushToko="";
                String norutToko = (jsonArrayUpper.getJSONObject(i).getString("norut_tb_toko"));
                String ratToko = (jsonArrayUpper.getJSONObject(i).getString("rat_tb_toko"));
                String distToko = (jsonArrayUpper.getJSONObject(i).getString("dist_tb_toko"));
                pushToko = "Toko "+norutToko+", rating : "+ratToko+", distance (in meters) : "+distToko;
                elem.add(pushToko);
            }
        }
        catch (JSONException e){
            logg(e.toString());
        }

        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elem));
        listview.setOnItemClickListener(this);
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


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
            Intent intent = new Intent();
            intent.setClass(this, MapsActivity.class);
            intent.putExtra("position", position);

            intent.putExtra("id", id);
            startActivity(intent);
    }
}
