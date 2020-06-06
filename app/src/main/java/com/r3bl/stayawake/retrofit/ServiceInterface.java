package com.r3bl.stayawake.retrofit;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ServiceInterface {


    String LOCATION_API = "/1.1/geo/reverse_geocode.json?lat=37.781157&long=-122.398720&granularity=neighborhood";


    String TWEET_API = "/1.1/statuses/user_timeline.json?count=10&screen_name=twitterapi";


//    enter your authorization bearer into this field
       @GET(TWEET_API)
    Call<ResponseBody> getTweetFeeds();


    @GET(LOCATION_API)
    Call<ResponseBody> getTweetsFeed(@Query("lat") Double lat, @Query("long") Double lon);


}
