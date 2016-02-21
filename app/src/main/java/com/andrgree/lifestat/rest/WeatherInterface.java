package com.andrgree.lifestat.rest;

import retrofit.Call;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by grag on 2/13/16.
 */
public interface WeatherInterface {

    @POST("/data/2.5/weather")
    Call<WeatherInfo> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String appid);

    @POST("/data/2.5/weather")
    Call<WeatherInfo> getWeather(@Query("city") String city, @Query("appid") String appid);
}
