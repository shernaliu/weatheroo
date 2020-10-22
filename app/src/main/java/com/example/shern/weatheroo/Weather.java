package com.example.shern.weatheroo;

/**
 * Created by shern on 6/10/2016.
 */

public class Weather {
    private String airportCode;
    private String temperature;
    private String description;
    private String heatIndex;
    private String windDirection;
    private String windSpeed;
    private String windChill;
    private String visibility;
    private String feelsLike;
    private String timeStamp;
    private String imageNo;

    public Weather() {
    }

    public Weather(String airportCode, String temperature, String description, String heatIndex, String windDirection, String windSpeed, String windChill, String visibility, String feelsLike, String timeStamp, String imageNo) {
        this.airportCode = airportCode;
        this.temperature = temperature;
        this.description = description;
        this.heatIndex = heatIndex;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.windChill = windChill;
        this.visibility = visibility;
        this.feelsLike = feelsLike;
        this.timeStamp = timeStamp;
        this.imageNo = imageNo;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeatIndex() {
        return heatIndex;
    }

    public void setHeatIndex(String heatIndex) {
        this.heatIndex = heatIndex;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindChill() {
        return windChill;
    }

    public void setWindChill(String windChill) {
        this.windChill = windChill;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageNo() {
        return imageNo;
    }

    public void setImageNo(String imageNo) {
        this.imageNo = imageNo;
    }
}
