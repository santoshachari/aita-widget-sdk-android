package com.aita.weatherwidget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aita.aitawidgetlibrary.model.WidgetAirport;
import com.aita.aitawidgetlibrary.model.WidgetFlight;
import com.aita.aitawidgetlibrary.view.WidgetView;
import com.aita.weatherwidget.R;
import com.aita.weatherwidget.other.AirportWeather;
import com.aita.weatherwidget.other.GetTemperatureTask;
import com.aita.weatherwidget.other.LatLng;

import java.util.ArrayList;
import java.util.List;

public class WeatherWidget extends WidgetView implements GetTemperatureTask.TemperatureListener {

    private View mErrorBlock;
    private View mWeatherBlock;
    private ProgressBar mProgressBar;

    private WidgetAirport mDepartureAirport;
    private WidgetAirport mArrivalAirport;

    public WeatherWidget(Context context) {
        super(context);
    }

    public WeatherWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(WidgetFlight flight) {
        mErrorBlock = findViewById(R.id.error_block);
        mWeatherBlock = findViewById(R.id.weather_block);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        update(flight);
    }

    @Override
    public void update(WidgetFlight flight) {
        mDepartureAirport = flight.getDepartureAirport();
        mArrivalAirport = flight.getArrivalAirport();

        if (mDepartureAirport != null && mArrivalAirport != null) {
            mProgressBar.setVisibility(VISIBLE);
            final LatLng departureAirportLatLng = new LatLng(
                    mDepartureAirport.getLatitude(),
                    mDepartureAirport.getLongitude());

            final LatLng arrivalAirportLatLng = new LatLng(
                    mArrivalAirport.getLatitude(),
                    mArrivalAirport.getLongitude());

            final GetTemperatureTask temperatureTask = new GetTemperatureTask(this);
            temperatureTask.execute(
                    departureAirportLatLng,
                    arrivalAirportLatLng);
        } else {
            mProgressBar.setVisibility(GONE);
            mErrorBlock.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onTemperatureLoaded(List<Integer> result) {
        mProgressBar.setVisibility(GONE);
        if (result.isEmpty() || result.size() < 2) {
            mErrorBlock.setVisibility(VISIBLE);
        } else {
            final ArrayList<AirportWeather> airports = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                final int temp = result.get(i);

                final int imageId;
                if (temp <= 5)
                    imageId = R.drawable.ic_snowflake;
                else if (temp <= 20)
                    imageId = R.drawable.ic_cloud;
                else
                    imageId = R.drawable.ic_sun;

                final String tempStr = temp <= 0 ? String.valueOf(temp) : "+" + temp;
                final String place = i == 0 ?
                        mDepartureAirport.getCode() : mArrivalAirport.getCode();

                airports.add(new AirportWeather(imageId, tempStr, place));
            }

            setUpWeatherBlock(airports);

            mWeatherBlock.setVisibility(VISIBLE);
        }
    }

    private void setUpWeatherBlock(ArrayList<AirportWeather> airports) {
        AirportWeather departureWeather = airports.get(0);
        AirportWeather arrivalWeather = airports.get(1);

        ((ImageView) findViewById(R.id.departure_weather_icon))
                .setImageResource(departureWeather.imageId);
        ((TextView) findViewById(R.id.departure_temperature_text))
                .setText(departureWeather.temperature);
        ((TextView) findViewById(R.id.departure_place_text)).setText(departureWeather.place);

        ((ImageView) findViewById(R.id.arrival_weather_icon))
                .setImageResource(arrivalWeather.imageId);
        ((TextView) findViewById(R.id.arrival_temperature_text))
                .setText(arrivalWeather.temperature);
        ((TextView) findViewById(R.id.arrival_place_text)).setText(arrivalWeather.place);
    }

    @Override
    public String getWidgetTitleText() {
        return getContext().getString(R.string.widget_title);
    }

    @Override
    public String getWidgetSubtitleText() {
        return getContext().getString(R.string.widget_subtitle);
    }

    @Override
    public int getWidgetIconId() {
        return R.drawable.ic_widget_icon;
    }

    @Override
    public int getWidgetViewId() {
        return R.layout.view_sample_widget;
    }

    @Override
    public OnClickListener getOnCardClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Winter is coming", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public boolean isCardClickable() {
        return true;
    }

}
