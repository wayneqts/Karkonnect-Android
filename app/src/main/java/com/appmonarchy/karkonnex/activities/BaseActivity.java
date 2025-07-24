package com.appmonarchy.karkonnex.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.authentication.GetStarted;
import com.appmonarchy.karkonnex.api.APIService;
import com.appmonarchy.karkonnex.api.APIUtils;
import com.appmonarchy.karkonnex.databinding.PopupReportBinding;
import com.appmonarchy.karkonnex.helper.AppSharedPreferences;
import com.appmonarchy.karkonnex.helper.Tool;

public class BaseActivity extends AppCompatActivity {
    public Tool tool;
    public APIService api;
    public AppSharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        tool = new Tool(this);
        api = APIUtils.getAPIService();
        pref = new AppSharedPreferences(this);
    }

    // popup logout
    public void popupLogout(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.do_you_want_to_logout))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    logout();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // popup report
    public void popupReport(){
        Dialog dialog = new Dialog(this);
        PopupReportBinding reportBinding = PopupReportBinding.inflate(getLayoutInflater());
        dialog.setContentView(reportBinding.getRoot());
        tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.WRAP_CONTENT);

        reportBinding.btBack.setOnClickListener(v -> dialog.dismiss());
        reportBinding.tvReason1.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.thanks_for_your_report))
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        dialog.dismiss();
                    });
            AlertDialog alert = alertDialog.create();
            alert.show();
        });
        reportBinding.tvReason2.setOnClickListener(v -> reportBinding.tvReason1.performClick());
        reportBinding.tvReason3.setOnClickListener(v -> reportBinding.tvReason1.performClick());

        dialog.show();
    }

    // logout
    public void logout(){
        pref.removeValue("user_id");
        startActivity(new Intent(this, GetStarted.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
