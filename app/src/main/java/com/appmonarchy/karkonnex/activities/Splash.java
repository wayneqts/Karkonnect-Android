package com.appmonarchy.karkonnex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.authentication.GetStarted;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            if (pref.getUserId().equals("")){
                startActivity(new Intent(this, GetStarted.class));
            }else {
                startActivity(new Intent(this, Home.class));
            }
            finish();
        }, 2000);
    }
}