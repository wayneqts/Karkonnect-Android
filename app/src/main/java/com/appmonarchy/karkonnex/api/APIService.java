package com.appmonarchy.karkonnex.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {
    @POST("login_submit.php")
    Call<JsonObject> login(@Body RequestBody rqBody);
    @GET()
    Call<JsonArray> getArrById(@Url String url, @Query("id") String id);
    @GET()
    Call<JsonArray> getArrBysId(@Url String url, @Query("sid") String id);
    @GET()
    Call<JsonArray> getArrByRqBody(@Url String url, @Body RequestBody rqBody);
    @GET()
    Call<JsonArray> search(@Url String url, @Query("state") String state, @Query("city") String city, @Query("zip") String zip, @Query("type") String type, @Query("country") String country);
    @POST("register_submit.php")
    Call<JsonObject> register(@Body RequestBody rqBody);
    @POST("forget_submit.php")
    Call<JsonObject> resetPw(@Body RequestBody rqBody);
    @POST()
    Call<JsonObject> postData(@Url String url, @Body RequestBody rqBody);
    @GET()
    Call<JsonObject> getDataById(@Url String url,  @Query("id") String id);
    @GET()
    Call<JsonArray> getDataArr(@Url String url);

    @GET("msglist.php")
    Call<JsonObject> getMessList(@Query("sid") String sid, @Query("rid") String rid);
    @GET("delete_account.php")
    Call<JsonObject> deleteAcc(@Query("id") String id);
}
