package com.example.shern.weatheroo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "tag1";
    public static JSONObject rawJsonObject = null;
    private static Button btnSearch;
    private static Button btnClear;
    EditText et_userInput;
    String code, name, city, country;
    ArrayList<Airport> airportList = new ArrayList<Airport>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnClickButtonListener();
    }

    public void OnClickButtonListener() {
        btnSearch = (Button) findViewById(R.id.btn_Search);
        btnClear = (Button) findViewById(R.id.btn_Clear);
        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Retrieve user's country input
                        et_userInput = (EditText) findViewById(R.id.et_userInput);
                        String userCountryInput = et_userInput.getText().toString();

                        //Validation of user country input (only match characters, no numbers)
                        if (userCountryInput.matches("^[a-zA-z]*$") && !userCountryInput.equals("")) {

                            //Display "Please wait..."
                            Toast.makeText(MainActivity.this,
                                    "Searching...", Toast.LENGTH_SHORT).show();

                            //insert user's input into the URL string
                            String stringUrl = "https://airport.api.aero/airport/match/" + userCountryInput + "?user_key=3035d833bb6e531654a3cce03e6b1fde";

                            //--------------HTTP GET REQUEST - START-----------------
                            ConnectivityManager connMgr = (ConnectivityManager)
                                    getSystemService(Context.CONNECTIVITY_SERVICE);

                            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                            //Check if phone has internet connection before executing HTTP GET
                            if (networkInfo != null && networkInfo.isConnected()) {
                                new DownloadWebpageTask().execute(stringUrl);
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Please turn on internet connection", Toast.LENGTH_SHORT).show();
                            }
                            //--------------HTTP GET REQUEST - END-----------------


                        } else {
                            //user input did not pass validation, display Toast message
                            Toast.makeText(MainActivity.this,
                                    "Please enter valid airport input!", Toast.LENGTH_SHORT).show();

                        }

                        // automatically hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(btnSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                }
        );

        // clear the input and list
        btnClear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_userInput.setText("");
                        displayListView(new ArrayList<Airport>(), new ArrayList<String>());
                    }
                });
    }

    public void displayListView(final ArrayList<Airport> airportList, ArrayList<String> airportNameList) {
        //Retrieve the list view
        final ListView lv = (ListView) findViewById(R.id.lv_airport);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                airportNameList);

        lv.setAdapter(adapter);

        //Un-hide the ListView
        lv.setVisibility(View.VISIBLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object o = lv.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                //Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

                //When user clicks an item in the listview, search for the IATA code in airportList
                //to retrieve IATA code
                for (Airport a : airportList) {

                    String aName = a.getName();

                    if (str.equals(aName)) {

                        Log.d("tag8", str);
                        Log.d("tag9", aName);

                        Intent intent = new Intent("com.example.shern.weatheroo.WeatherActivity");

                        intent.putExtra("airportCode", a.getCode());

                        startActivity(intent);

                    }

                }

            }
        });

        Log.d("tagDisplayListView", "Successfully executed displayListView()");
    }


    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        //TextView tv1 = (TextView) findViewById(R.id.textView);

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {

                //this should never happen
                Toast.makeText(MainActivity.this,
                        "Unable to retrieve web page. URL may be invalid.", Toast.LENGTH_SHORT).show();

                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        // onPostExecute displays the results of the AsyncTask.
        // and all subsequent codes are written here lolol
        @Override
        protected void onPostExecute(String result) {

            Log.d("tag3", "HAY!");

            //Throw away the "callback(" & last ) in the string
            String cleanString = cleanJsonString(result);
            //tv1.setText(cleanString);

            Log.d("tag4", cleanString);

            //parse json String to json Object & store in rawJsonObject
            //inside the rawJsonObject, there is an array of Json Objects containing all the
            //airport details
            rawJsonObject = parseToJsonObj(cleanString);


            //extract "airports" data array from json string
            try {
                //get jsonArray from rawJsonObject
                JSONArray ja_data = rawJsonObject.getJSONArray("airports");
                int length = ja_data.length();

                Log.d("tag7", "airport array length: " + Integer.toString(length));

                ArrayList<String> airportNameList = new ArrayList<>();

                //loop through the jsonarray to create Airport objects & store in an arraylist
                for (int i = 0; i < ja_data.length(); i++) {
                    JSONObject row = ja_data.getJSONObject(i);
                    code = row.getString("code");
                    name = row.getString("name");
                    city = row.getString("city");
                    country = row.getString("country");

                    //Create new Airport object using the extracted Airport information
                    Airport a = new Airport(code, name, city, country);
                    airportList.add(a);

                    //add the airport's name to airportNameList for the ListView later
                    airportNameList.add(name);
                }
                displayListView(airportList, airportNameList);


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,
                        "Something went wrong!", Toast.LENGTH_SHORT).show();

            }


        }
    }

    // Given a URL, establish an HttpUrlConnection and retrieve
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
//            String contentAsString = readIt(is, len);
            String contentAsString = getStringFromInputStream(is);

            //Log.d("tag2", contentAsString);

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    /**
     * Because the Airport API does not return a pure JSON string,
     * we need to remove "callback(" at the front of the string and
     * the last ) at the back, to get a pure JSON string
     *
     * @param dirtyString dirty dirty string!!!
     * @return pure Json String
     */
    private static String cleanJsonString(String dirtyString) {

        //remove callback(
        String cleanString = dirtyString.substring(9);

        //remove last )
        return cleanString.substring(0, cleanString.length() - 1);
    }

    //this method parse the json String to json Object
    private static JSONObject parseToJsonObj(String jsonString) {

        JSONObject jsObj = null;

        try {
            jsObj = new JSONObject(jsonString);
            Log.d("tag5", "Successfully parsed json string to json object!");
        } catch (Throwable t) {
            Log.e("errorTag", "Could not parse malformed JSON: \"" + jsonString + "\"");
        }

        return jsObj;
    }


}
