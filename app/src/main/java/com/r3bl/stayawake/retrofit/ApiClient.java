package com.r3bl.stayawake.retrofit;

import android.content.Context;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;


public class ApiClient {

    public static final String SERVER_LOGIN = "https://api.twitter.com/";

    public static final String BASE_URL = SERVER_LOGIN;

    private static Retrofit retrofit = null;

    private ApiClient() {


        if (retrofit != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static synchronized Retrofit getClient(Context context) {


        if (retrofit == null) {


            OkHttpClient client = new OkHttpClient.Builder().
                    readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();

            return retrofit;
        }


        return retrofit;
    }


}
