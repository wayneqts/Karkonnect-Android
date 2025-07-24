package com.appmonarchy.karkonnex.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.EditPf;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.activities.authentication.GetStarted;
import com.appmonarchy.karkonnex.databinding.FrmMyPfBinding;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.Profile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMyPf extends Fragment {
    FrmMyPfBinding binding;
    Home activity;
    Profile pf;
    public FrmMyPf() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmMyPfBinding.inflate(getLayoutInflater());
        init();

        binding.btEdit.setOnClickListener(v -> startActivityForResult(new Intent(activity, EditPf.class).putExtra("data", pf), AppConstrains.REFRESH_PF_CODE));
        binding.btDeleteAcc.setOnClickListener(v -> new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.app_name))
                .setMessage(activity.getString(R.string.asd_delete_account))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    delete();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show());

        return binding.getRoot();
    }

    // init UI
    private void init(){
        activity.tool.showLoading();
        getPf();
    }

    // get profile
    private void getPf(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", activity.pref.getUserId());
        activity.api.postData("view_profile.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        JSONObject jsData = jsonObject.optJSONObject("data");
                        if (jsonObject.optBoolean("status")){
                            if (jsData != null){
                                pf = new Profile(jsData.optString("status"), jsData.optString("fname"), jsData.optString("lname"), jsData.optString("username"),
                                        jsData.optString("email"), jsData.optString("phone"), jsData.optString("phone_status"), jsData.optString("address"),
                                        jsData.optString("city"), jsData.optString("zip"), jsData.optString("img"));
                                Glide.with(activity).load(jsData.optString("img")).error(R.drawable.no_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.iv);
                                binding.tvFName.setText(jsData.optString("fname"));
                                binding.tvLName.setText(jsData.optString("lname"));
                                binding.tvPhone.setText(jsData.optString("phone"));
                                binding.tvUsername.setText(jsData.optString("username"));
                                binding.tvEmail.setText(jsData.optString("email"));
                                binding.tvAddress.setText(jsData.optString("address"));
                                binding.tvCity.setText(jsData.optString("city"));
                                binding.tvZip.setText(jsData.optString("zip"));
                                binding.tvCountry.setText(jsData.optString("country"));
                                binding.tvState.setText(jsData.optString("state"));
                                binding.llMain.setVisibility(View.VISIBLE);
                            }
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

    // delete account
    private void delete(){
        activity.api.deleteAcc(activity.pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("Status")){
                            Toast.makeText(activity, jsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
                            activity.logout();
                            startActivity(new Intent(activity, GetStarted.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}