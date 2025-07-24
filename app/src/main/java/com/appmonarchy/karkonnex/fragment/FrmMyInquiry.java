package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.adapter.pager.InquiryAdapter;
import com.appmonarchy.karkonnex.databinding.FrmMyInquiryBinding;


public class FrmMyInquiry extends Fragment {
    FrmMyInquiryBinding binding;
    Home activity;
    InquiryAdapter adapter;
    public FrmMyInquiry() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmMyInquiryBinding.inflate(getLayoutInflater());

        adapter = new InquiryAdapter(getChildFragmentManager(), getLifecycle());
        binding.vpInquiry.setAdapter(adapter);
        binding.rgTab.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_received){
                binding.vpInquiry.setCurrentItem(0);
            }else {
                binding.vpInquiry.setCurrentItem(1);
            }
        });

        return binding.getRoot();
    }
}