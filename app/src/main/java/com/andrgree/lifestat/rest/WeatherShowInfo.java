package com.andrgree.lifestat.rest;

import com.andrgree.lifestat.db.table.CheckTimestamp;

public class WeatherShowInfo {

    private String temperature;
    private String pressure;
    private String moodDay;
    private String humidity;
    private String wind;

    public WeatherShowInfo () {};

    public WeatherShowInfo (WeatherInfo weatherInfo) {
        //Temperature = weatherInfo.get
        this.setTemperature(new Float(weatherInfo.getMain().getTemp()).toString());
        this.setPressure(new Float(weatherInfo.getMain().getPressure()).toString());
        this.setHumidity(new Integer(weatherInfo.getMain().getHumidity()).toString());
        this.setWind(new Float(weatherInfo.getWind().getSpeed()).toString());
        this.setMoodDay("0");
    };

    public WeatherShowInfo (CheckTimestamp checkTimestamp) {

        this.setTemperature(checkTimestamp.getTemperature());
        this.setPressure(checkTimestamp.getPressure());
        this.setHumidity(checkTimestamp.getHumidity());
        this.setWind(checkTimestamp.getWind());
        this.setMoodDay("0");
    };

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getMoodDay() {
        return moodDay;
    }

    public void setMoodDay(String moodDay) {
        this.moodDay = moodDay;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }
}
