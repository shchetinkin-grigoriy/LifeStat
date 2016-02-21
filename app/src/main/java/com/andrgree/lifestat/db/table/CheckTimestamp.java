package com.andrgree.lifestat.db.table;

import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для хранения временных отметок ввода данных для пользователя
 */
public class CheckTimestamp {

    public static final String TABLE = "check_timestamp";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_MOON_DAY = "mood_day";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_WIND = "wind";
    public static final String COLUMN_TS = "create_date";

    private long id;
    private String temperature;
    private String pressure;
    private String moonDay;
    private String humidity;
    private String wind;
    private Calendar ts;

    public static CheckTimestamp fromCursor(Cursor cursor) {
        CheckTimestamp ts = new CheckTimestamp();
        ts.setId(cursor.getInt(0));
        ts.setTemperature(cursor.getString(1));
        ts.setPressure(cursor.getString(2));
        ts.setMoonDay(cursor.getString(3));
        ts.setHumidity(cursor.getString(4));
        ts.setWind(cursor.getString(5));
        //statParam.setCreateDate(cursor.getString()Int(2));

        return ts;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getMoonDay() {
        return moonDay;
    }

    public void setMoonDay(String moonDay) {
        this.moonDay = moonDay;
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

    public Calendar getTs() {
        return ts;
    }

    public void setTs(Calendar ts) {
        this.ts = ts;
    }
}