package com.appmonarchy.karkonnex.fragment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.databinding.FrmContactBinding;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmContact extends Fragment {
    FrmContactBinding binding;
    Home activity;
    public FrmContact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmContactBinding.inflate(getLayoutInflater());

        binding.btSend.setOnClickListener(v -> {
            String name = binding.etFullname.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String msg = binding.etMess.getText().toString().trim();
            if (TextUtils.isEmpty(name)){
                Toast.makeText(activity, activity.getString(R.string.full_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(phone)){
                Toast.makeText(activity, activity.getString(R.string.phone_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(email)){
                Toast.makeText(activity, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!activity.tool.isEmailValid(email)){
                Toast.makeText(activity, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(msg)){
                Toast.makeText(activity, activity.getString(R.string.messages_is_required), Toast.LENGTH_SHORT).show();
            }else {
                contact();
            }
        });

        return binding.getRoot();
    }

    // contact
    private void contact(){
        activity.tool.showLoading();
        WifiManager wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("name", binding.etFullname.getText().toString().trim())
                .addFormDataPart("email", binding.etEmail.getText().toString().trim())
                .addFormDataPart("msg", binding.etMess.getText().toString().trim())
                .addFormDataPart("phone", binding.etPhone.getText().toString().trim())
                .addFormDataPart("ip_address", ip);
        activity.api.postData("contact.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                activity.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                activity.tool.hideLoading();
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}