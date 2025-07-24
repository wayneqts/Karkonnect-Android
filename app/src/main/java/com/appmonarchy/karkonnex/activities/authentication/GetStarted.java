package com.appmonarchy.karkonnex.activities.authentication;

import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.activities.Inquiry;
import com.appmonarchy.karkonnex.adapter.ShowCaseAdapter;
import com.appmonarchy.karkonnex.databinding.ActivityGetStartedBinding;
import com.appmonarchy.karkonnex.model.ShowCase;

import java.util.ArrayList;
import java.util.List;

public class GetStarted extends BaseActivity {
    ActivityGetStartedBinding binding;
    List<ShowCase> list;
    ShowCaseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btToRegister.setOnClickListener(v -> startActivity(new Intent(this, SignUp.class)));
        binding.btToLogin.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        binding.btToInquire.setOnClickListener(v -> startActivity(new Intent(this, Inquiry.class)));
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        setupList();
        adapter = new ShowCaseAdapter(this, list);
        binding.rcv.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rcv.setAdapter(adapter);
    }

    // setup list
    private void setupList(){
        list.add(new ShowCase("Chevrolet Camaro", "Fairfax, VA", "$50 / Day", R.drawable.car0));
        list.add(new ShowCase("Toyota Prius", "San francisco, CA", "$4950 / Sale", R.drawable.car1));
        list.add(new ShowCase("Lexus", "Philpadephlia, PA", "$12,990 / Day", R.drawable.car2));
        list.add(new ShowCase("Hyundai Elantra", "Washington, DC", "$14,500 / Sale", R.drawable.car3));
        list.add(new ShowCase("Audi", "Boston, MA", "$40 / Day", R.drawable.car4));
        list.add(new ShowCase("Toyota Sienna", "New York, NY", "$9000 / Sale", R.drawable.car5));
        list.add(new ShowCase("Grand Gherokee 2016", "Houston, TX", "$50 / Day", R.drawable.car6));
        list.add(new ShowCase("Volkswagen", "Chicago, IL", "$35 / Day", R.drawable.car7));
    }
}