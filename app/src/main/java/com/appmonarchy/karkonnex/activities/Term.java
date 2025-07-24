package com.appmonarchy.karkonnex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityTermBinding;

public class Term extends BaseActivity {
    ActivityTermBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
    }
}