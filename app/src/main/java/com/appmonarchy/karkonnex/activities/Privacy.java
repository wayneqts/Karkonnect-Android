package com.appmonarchy.karkonnex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityPrivacyBinding;

public class Privacy extends BaseActivity {
    ActivityPrivacyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
    }
}