package com.example.ecommerce.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClaudeApi {
    private static final String TAG = "ClaudeApi";
    private static final String BASE_URL = "https://api.anthropic.com/v1/";
    private static ClaudeApiService apiService;
    
    public static ClaudeApiService getApiService() {
        if (apiService == null) {
            // Create logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Create OkHttp client with logging and API key interceptors
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        String apiKey = System.getenv("CLAUDE_API_KEY");
                        if (apiKey == null || apiKey.isEmpty()) {
                            apiKey = "your_default_api_key"; // Replace with your default key
                        }
                        
                        return chain.proceed(chain.request().newBuilder()
                                .addHeader("x-api-key", apiKey)
                                .addHeader("anthropic-version", "2023-06-01")
                                .addHeader("content-type", "application/json")
                                .build());
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            
            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            // Create API service
            apiService = retrofit.create(ClaudeApiService.class);
        }
        
        return apiService;
    }
}
