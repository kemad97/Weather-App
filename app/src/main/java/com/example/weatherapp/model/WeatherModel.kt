package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Response(

    @field:SerializedName("city")
    val city: City? = null,

    @field:SerializedName("cnt")
    val cnt: Int? = null,

    @field:SerializedName("cod")
    val cod: String? = null,

    @field:SerializedName("message")
    val message: Int? = null,

    @field:SerializedName("list")
    val list: List<ListItem?>? = null
)

data class WeatherItem(

    @field:SerializedName("icon")
    val icon: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("main")
    val main: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class Wind(

    @field:SerializedName("deg")
    val deg: Int? = null,

    @field:SerializedName("speed")
    val speed: Any? = null,

    @field:SerializedName("gust")
    val gust: Any? = null
)

data class City(

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("coord")
    val coord: Coord? = null,

    @field:SerializedName("sunrise")
    val sunrise: Int? = null,

    @field:SerializedName("timezone")
    val timezone: Int? = null,

    @field:SerializedName("sunset")
    val sunset: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("population")
    val population: Int? = null
)

data class Clouds(

    @field:SerializedName("all")
    val all: Int? = null
)

data class Coord(

    @field:SerializedName("lon")
    val lon: Any? = null,

    @field:SerializedName("lat")
    val lat: Any? = null
)

data class Main(

    @field:SerializedName("temp")
    val temp: Any? = null,

    @field:SerializedName("temp_min")
    val tempMin: Any? = null,

    @field:SerializedName("grnd_level")
    val grndLevel: Int? = null,

    @field:SerializedName("temp_kf")
    val tempKf: Any? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("pressure")
    val pressure: Int? = null,

    @field:SerializedName("sea_level")
    val seaLevel: Int? = null,

    @field:SerializedName("feels_like")
    val feelsLike: Any? = null,

    @field:SerializedName("temp_max")
    val tempMax: Any? = null
)

data class ListItem(

    @field:SerializedName("dt")
    val dt: Int? = null,

    @field:SerializedName("pop")
    val pop: Double? = null,

    @field:SerializedName("visibility")
    val visibility: Int? = null,

    @field:SerializedName("dt_txt")
    val dtTxt: String? = null,

    @field:SerializedName("weather")
    val weather: List<WeatherItem?>? = null,

    @field:SerializedName("main")
    val main: Main? = null,

    @field:SerializedName("clouds")
    val clouds: Clouds? = null,

    @field:SerializedName("sys")
    val sys: Sys? = null,

    @field:SerializedName("wind")
    val wind: Wind? = null
)

data class Sys(

    @field:SerializedName("pod")
    val pod: String? = null
)


