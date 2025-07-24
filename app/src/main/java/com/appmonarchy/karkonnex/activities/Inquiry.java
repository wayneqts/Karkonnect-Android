package com.appmonarchy.karkonnex.activities;

import android.content.Intent;
import android.os.Bundle;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.authentication.Login;
import com.appmonarchy.karkonnex.activities.authentication.SignUp;
import com.appmonarchy.karkonnex.databinding.ActivityGetStartedBinding;
import com.appmonarchy.karkonnex.databinding.ActivityInquiryBinding;
import com.appmonarchy.karkonnex.databinding.FrmInquireBinding;

public class Inquiry extends BaseActivity{
    ActivityInquiryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
        binding.btRent.setOnClickListener(v -> tool.openWeb(getString(R.string.rent_url)));
        binding.btBuy.setOnClickListener(v -> tool.openWeb(getString(R.string.buy_url)));
        binding.btSell.setOnClickListener(v -> tool.openWeb(getString(R.string.sell_url)));
    }
}
