package com.example.shern.weatheroo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WeatherActivity extends AppCompatActivity {
    Weather w = new Weather();
    DBAdaptor db = new DBAdaptor(this);
    TextView tv_input_airportCode;
    TextView tv_input_temperature;
    TextView tv_input_description;
    TextView tv_input_heatIndex;
    TextView tv_input_windDirection;
    TextView tv_input_windSpeed;
    TextView tv_input_windChill;
    TextView tv_input_visibility;
    TextView tv_input_feelsLike;
    TextView tv_input_timeStamp;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tv_input_airportCode = (TextView) findViewById(R.id.tv_input_airportCode);
        tv_input_temperature = (TextView) findViewById(R.id.tv_input_temperature);
        tv_input_description = (TextView) findViewById(R.id.tv_input_description);
        tv_input_heatIndex = (TextView) findViewById(R.id.tv_input_heatIndex);
        tv_input_windDirection = (TextView) findViewById(R.id.tv_input_windDirection);
        tv_input_windSpeed = (TextView) findViewById(R.id.tv_input_windSpeed);
        tv_input_windChill = (TextView) findViewById(R.id.tv_input_windChill);
        tv_input_visibility = (TextView) findViewById(R.id.tv_input_visibility);
        tv_input_feelsLike = (TextView) findViewById(R.id.tv_input_feelsLike);
        tv_input_timeStamp = (TextView) findViewById(R.id.tv_input_timeStamp);
        icon = (ImageView) findViewById(R.id.icon);

        Intent intent = getIntent();
        String airportCode = intent.getStringExtra("airportCode");
        loadWeatherDetails(airportCode);
    }

    /**
     * this method do the HTTP GET Request & retrieves the JSON object
     *
     * @param airportCodeInput 3-letter airport code the User has input
     */
    public void loadWeatherDetails(String airportCodeInput) {

        //Sample url https://weather-qa.api.aero/weather/v1/current/SIN?temperatureScale=C&lengthUnit=K
        String url = "https://weather-qa.api.aero/weather/v1/current/" + airportCodeInput + "?temperatureScale=C&lengthUnit=K";

        //Doing the HTML GET REQUEST PART
        //Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.toString());

                        try {
                            // response can either be success or fails
                            if ((Boolean) response.get("success") == true) {
                                w.setAirportCode(response.getJSONObject("currentWeather").getString("location"));
                                w.setTemperature(response.getJSONObject("currentWeather").getString("temperature"));
                                w.setDescription(response.getJSONObject("currentWeather").getString("phrase"));
                                w.setHeatIndex(response.getJSONObject("currentWeather").getString("heatIndex"));
                                w.setWindDirection(response.getJSONObject("currentWeather").getString("windDirection"));
                                w.setWindSpeed(response.getJSONObject("currentWeather").getString("windSpeed"));
                                w.setWindChill(response.getJSONObject("currentWeather").getString("windChill"));
                                w.setVisibility(response.getJSONObject("currentWeather").getString("visibility"));
                                w.setFeelsLike(response.getJSONObject("currentWeather").getString("feelsLikeTemperature"));
                                w.setTimeStamp(response.getJSONObject("currentWeather").getString("timeStamp"));
                                w.setImageNo(response.getJSONObject("currentWeather").getString("icon"));

                                //Display weather details
                                tv_input_airportCode.setText(w.getAirportCode());
                                tv_input_temperature.setText(w.getTemperature() + "°C");
                                tv_input_description.setText(w.getDescription());
                                tv_input_heatIndex.setText(w.getHeatIndex() + "°C");
                                tv_input_windDirection.setText(w.getWindDirection());
                                tv_input_windSpeed.setText(w.getWindSpeed() + "km/h");
                                tv_input_windChill.setText(w.getWindChill() + "°C");
                                tv_input_visibility.setText(w.getVisibility() + "km");
                                tv_input_feelsLike.setText(w.getFeelsLike() + "°C");
                                tv_input_timeStamp.setText(w.getTimeStamp());

                                Log.d("image", w.getImageNo());

                                // load icon
                                String url = "https://uds-static.api.aero/weather/icon/lg/" + w.getImageNo() + ".png";
                                Picasso.with(db.context)
                                        .load(url)
                                        .resize(250, 250)
                                        .into(icon);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                                builder.setMessage("No Weather Data")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Data not available.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-apiKey", "89e15931434731aefdaa04920ec60e44");
                return headers;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(req);
    }

}
