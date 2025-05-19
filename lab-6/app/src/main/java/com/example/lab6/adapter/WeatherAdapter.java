package com.example.lab6.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.lab6.R;
import com.example.lab6.model.Weather;
import com.squareup.picasso.Picasso;
import java.util.List;

public class WeatherAdapter extends ArrayAdapter<Weather> {

    private Context context;
    private List<Weather> weatherList;

    public WeatherAdapter(@NonNull Context context, List<Weather> weatherList) {
        super(context, R.layout.weather_item, weatherList);
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        }

        Weather weather = weatherList.get(position);

        ImageView ivWeatherIcon = convertView.findViewById(R.id.ivWeatherIcon);
        TextView tvWeatherDescription = convertView.findViewById(R.id.tvWeatherDescription);
        TextView tvTemperature = convertView.findViewById(R.id.tvTemperature);
        TextView tvWindHumidity = convertView.findViewById(R.id.tvWindHumidity);

        // Load weather icon using Picasso
        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getWeather().get(0).getIcon() + "@2x.png";
        Picasso.get().load(iconUrl).into(ivWeatherIcon);

        // Set weather details
        tvWeatherDescription.setText(weather.getWeather().get(0).getDescription());
        tvTemperature.setText("Min: " + weather.getTemp().getMin() + "°C, Max: " + weather.getTemp().getMax() + "°C");
        tvWindHumidity.setText("Wind: " + weather.getWindSpeed() + " m/s, Humidity: " + weather.getHumidity() + "%");

        return convertView;
    }
}

