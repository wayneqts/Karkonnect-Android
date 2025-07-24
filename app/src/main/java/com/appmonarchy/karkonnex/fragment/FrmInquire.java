package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.databinding.FrmInquireBinding;


public class FrmInquire extends Fragment {
    FrmInquireBinding binding;
    Home home;
    public FrmInquire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = (Home) getActivity();
        binding = FrmInquireBinding.inflate(getLayoutInflater());

        binding.btRent.setOnClickListener(v -> home.tool.openWeb(getString(R.string.rent_url)));
        binding.btBuy.setOnClickListener(v -> home.tool.openWeb(getString(R.string.buy_url)));
        binding.btSell.setOnClickListener(v -> home.tool.openWeb(getString(R.string.sell_url)));
        return binding.getRoot();
    }
}