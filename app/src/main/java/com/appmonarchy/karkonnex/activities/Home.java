package com.appmonarchy.karkonnex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityHomeBinding;
import com.appmonarchy.karkonnex.fragment.FrmContact;
import com.appmonarchy.karkonnex.fragment.FrmHome;
import com.appmonarchy.karkonnex.fragment.FrmInquire;
import com.appmonarchy.karkonnex.fragment.FrmMess;
import com.appmonarchy.karkonnex.fragment.FrmMyCar;
import com.appmonarchy.karkonnex.fragment.FrmMyInquiry;
import com.appmonarchy.karkonnex.fragment.FrmMyPf;
import com.appmonarchy.karkonnex.fragment.FrmNotifications;
import com.appmonarchy.karkonnex.fragment.FrmSearch;
import com.appmonarchy.karkonnex.fragment.FrmTerm;
import com.appmonarchy.karkonnex.helper.AppConstrains;

import java.util.Calendar;

public class Home extends BaseActivity {
    ActivityHomeBinding binding;
    FragmentManager frmManager;
    String crFrm;
    int doubleBackToExit = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btToolbar.setOnClickListener(v -> binding.drawer.openDrawer(GravityCompat.START));
        binding.mnLogout.setOnClickListener(v -> popupLogout());
        binding.mnHome.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("home")){
                    binding.ivHome.setVisibility(View.VISIBLE);
                    binding.tvTitle.setVisibility(View.GONE);
                    replaceFrm(new FrmHome(), "home");
                }
            }, 200);
        });
        binding.mnSearch.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("search")){
                    clickMnWithTitle(getString(R.string.search_title));
                    replaceFrm(new FrmSearch(), "search");
                }
            }, 300);
        });
        binding.mnInquire.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("inquire")){
                    clickMnWithTitle(getString(R.string.inquire_rent_buy));
                    replaceFrm(new FrmInquire(), "inquire");
                }
            }, 300);
        });
        binding.mnContact.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("contact")){
                    clickMnWithTitle(getString(R.string.contact_us_title));
                    replaceFrm(new FrmContact(), "contact");
                }
            }, 200);
        });
        binding.mnTerm.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("term")){
                    clickMnWithTitle(getString(R.string.terms));
                    replaceFrm(new FrmTerm(), "term");
                }
            }, 200);
        });
        binding.mnPf.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("profile")){
                    clickMnWithTitle(getString(R.string.my_profile));
                    replaceFrm(new FrmMyPf(), "profile");
                }
            }, 300);
        });
        binding.mnCar.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("car")){
                    clickMnWithTitleBt(getString(R.string.my_cars));
                    replaceFrm(new FrmMyCar(), "car");
                }
            }, 300);
        });
        binding.mnMess.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("mess")){
                    clickMnWithTitle(getString(R.string.messages));
                    replaceFrm(new FrmMess(), "mess");
                }
            }, 300);
        });
        binding.mnNotifi.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("notification")){
                    clickMnWithTitle(getString(R.string.notification));
                    replaceFrm(new FrmNotifications(), "notification");
                }
            }, 300);
        });
        binding.mnInquiry.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("inquiry")){
                    clickMnWithTitle(getString(R.string.my_inquiry));
                    replaceFrm(new FrmMyInquiry(), "inquiry");
                }
            }, 300);
        });
        binding.btAdd.setOnClickListener(v -> startActivityForResult(new Intent(this, AddCar.class), AppConstrains.REFRESH_CAR_CODE));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.closeDrawer(GravityCompat.START);
                } else {
                    if (doubleBackToExit == 2) {
                        finishAffinity();
                        System.exit(0);
                    } else {
                        doubleBackToExit++;
                        Toast.makeText(Home.this, R.string.please_press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(() -> doubleBackToExit = 1, 2000);
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // init UI
    private void init(){
        frmManager = getSupportFragmentManager();
        Calendar c = Calendar.getInstance();
        binding.tvAppDes.setText("@"+c.get(Calendar.YEAR)+" "+getString(R.string.app_name)+" by App Monarchy, Inc.\nAll rights reserved");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolBar, R.string.drawer_open, R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        replaceFrm(new FrmHome(), "home");
//        getData();
    }

    // replace fragment
    public void replaceFrm(Fragment frm, String tag){
        frmManager.beginTransaction().replace(R.id.fl_main, frm).commit();
        crFrm = tag;
    }

    // click menu with title
    private void clickMnWithTitle(String title){
        binding.ivHome.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(title);
        binding.btAdd.setVisibility(View.GONE);
    }

    // click menu with title and button
    private void clickMnWithTitleBt(String title){
        binding.ivHome.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(title);
        binding.btAdd.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppConstrains.REFRESH_MESS_CODE){
            replaceFrm(new FrmMess(), "message");
        }
        if (resultCode == AppConstrains.REFRESH_CAR_CODE){
            replaceFrm(new FrmMyCar(), "car");
        }
        if (resultCode == AppConstrains.REFRESH_PF_CODE){
            replaceFrm(new FrmMyPf(), "profile");
        }
    }
}