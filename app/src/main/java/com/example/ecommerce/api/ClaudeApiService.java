package com.example.ecommerce.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClaudeApiService {
    /**
     * Get a response from Claude API.
     * This is a simplified implementation - in a real app, you would create proper request/response models.
     * 
     * @param message The user's message
     * @return A response from Claude
     */
    @POST("messages")
    Call<ResponseBody> getClaudeResponse(@Query("message") String message);
}
