package com.example.yogishadixit.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Create some dummy data for ListView
            ArrayList<String> forecasts = new ArrayList<String>();
            forecasts.add("Today - Sunny - 88/63");
            forecasts.add("Tomorrow - Foggy - 70/40");
            forecasts.add("Wednesday - Cloudy - 72/63");
            forecasts.add("Thursday - Asteroids - 75/65");
            forecasts.add("Friday - Heavy Rain - 65/56");
            forecasts.add("Saturday - HELP TRAPPED IN WEATHERSTATION - 60/51");
            forecasts.add("Sunday - Sunny - 80/68");

            ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecasts);

            ListView forecastView = (ListView) rootView.findViewById(R.id.listview_forecast);
            forecastView.setAdapter(forecastAdapter);

            // Open a HTTP connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // This will contain the raw JSON response as a string
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,USA&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing
                    return null;
                }

                forecastJsonStr = buffer.toString();
            }
            catch (IOException e) {
                Log.e("PlaceholderFragment", "Error: ", e);
                // If the code didn't successfully get the weather data, there is no point in trying
                // to parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return rootView;
        }
    }
}
