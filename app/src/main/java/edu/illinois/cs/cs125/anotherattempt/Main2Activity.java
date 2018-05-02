package edu.illinois.cs.cs125.anotherattempt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import edu.illinois.cs.cs125.anotherattempt.R;

import static edu.illinois.cs.cs125.anotherattempt.R.id.weatherColor;

public class Main2Activity extends AppCompatActivity {
    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;
    private static final String TAG = "Lab12:Main";
    public boolean goodWeather = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final TextView birdwords = findViewById(R.id.nameResult);
        if (MainActivity.birdResults.contains("bird")) {

            birdwords.setText(MainActivity.birdResults);
        } else {
            birdwords.setText("Not a bird");
        }
        requestQueue = Volley.newRequestQueue(this);
        startAPICall();
        String json = requestQueue.getCache().toString();

        JsonParser parser = new JsonParser();
        if (json == null) {
            Log.d(TAG, "null json return");
        }
      //  System.out.println("HEREHERE");
     //   System.out.println(json);
        if (json != null) {
            if (json.contains("rain") || json.contains("Rain") || json.contains("thunderstorm") || json.contains("snow")) {
                goodWeather = false;
            }
        }
        ImageView backgroundImg = (ImageView) findViewById(R.id.weatherColor);
        TextView weatherText = findViewById(R.id.weatherText);


        if (goodWeather) {
            backgroundImg.setBackgroundColor(Color.rgb(0, 255, 0));
            weatherText.setText("It is nice out to go birding!");
        } else {
            backgroundImg.setBackgroundColor(Color.rgb(255, 0, 0));
            weatherText.setText("It is not nice weather out to go birding");

        }

    }
        void startAPICall() {
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        "http://api.openweathermap.org/data/2.5/weather?zip=61820,us&appid="
                                + "6e5d1014aff7338796d117f71af02eb5",
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                try {
                                    Log.d(TAG, response.toString(2));
                                } catch (JSONException ignored) { }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
