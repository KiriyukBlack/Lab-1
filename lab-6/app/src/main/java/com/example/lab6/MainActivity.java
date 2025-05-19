package com.example.lab6;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lab6.adapter.WeatherAdapter;
import com.example.lab6.model.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button btnFetchWeather;
    private TextView tvCityName;
    private ListView lvWeather;
    private WeatherAdapter weatherAdapter;
    private List<Weather> weatherList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchWeather = findViewById(R.id.btnFetchWeather);
        tvCityName = findViewById(R.id.tvCityName);
        lvWeather = findViewById(R.id.lvWeather);

        // Initialize the adapter and set it to the ListView
        weatherAdapter = new WeatherAdapter(this, weatherList);
        lvWeather.setAdapter(weatherAdapter);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        btnFetchWeather.setOnClickListener(v -> {
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                fetchWeather(currentLatitude, currentLongitude);
            } else {
                Toast.makeText(MainActivity.this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Display latitude and longitude
                    Toast.makeText(MainActivity.this, "Lat: " + currentLatitude + ", Long: " + currentLongitude, Toast.LENGTH_SHORT).show();

                } else {

                }
            }
        });
    }

    private void fetchWeather(double latitude, double longitude) {
        String apiKey = "a085cf3f37f6dcb3b4e4bb6be1052838"; // Replace with your OpenWeather API key
        String urlString = "https://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&cnt=5&appid=" + apiKey + "&units=metric";

        new FetchWeatherTask().execute(urlString);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Extract city name and display it
                JSONObject cityObject = jsonObject.getJSONObject("city");
                String cityName = cityObject.getString("name");
                tvCityName.setText("City: " + cityName);

                JSONArray listArray = jsonObject.getJSONArray("list");
                weatherList.clear();
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject listObject = listArray.getJSONObject(i);

                    Weather weather = new Weather();
                    weather.setDt(listObject.getLong("dt"));

                    JSONObject tempObject = listObject.getJSONObject("temp");
                    Weather.Temp temp = weather.new Temp();
                    temp.setDay(tempObject.getDouble("day"));
                    temp.setMin(tempObject.getDouble("min"));
                    temp.setMax(tempObject.getDouble("max"));
                    weather.setWindSpeed(listObject.getDouble("speed"));
                    weather.setHumidity(listObject.getInt("humidity"));
                    weather.setTemp(temp);

                    JSONArray weatherArray = listObject.getJSONArray("weather");
                    List<Weather.WeatherDescription> weatherDescriptions = new ArrayList<>();
                    for (int j = 0; j < weatherArray.length(); j++) {
                        JSONObject weatherObject = weatherArray.getJSONObject(j);
                        Weather.WeatherDescription weatherDescription = weather.new WeatherDescription();
                        weatherDescription.setMain(weatherObject.getString("main"));
                        weatherDescription.setDescription(weatherObject.getString("description"));
                        weatherDescription.setIcon(weatherObject.getString("icon")); // Get weather icon
                        weatherDescriptions.add(weatherDescription);
                    }
                    weather.setWeather(weatherDescriptions);

                    weatherList.add(weather);
                }
                weatherAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
