package com.appmonarchy.karkonnex.api;

public class APIUtils {
    public static String URL_SERVER = "https://karkonnex.com/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(URL_SERVER).create(APIService.class);
    }
}
