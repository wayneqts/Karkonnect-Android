package com.appmonarchy.karkonnex.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public AppSharedPreferences(Context context) {
        this.context = context;
    }

    // save user id
    public void setUserId(String id){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("user_id", id).apply();
    }

    public String getUserId(){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        return pref.getString("user_id", "");
    }

    // remove value
    public void removeValue(String key){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.remove(key).apply();
    }
}
