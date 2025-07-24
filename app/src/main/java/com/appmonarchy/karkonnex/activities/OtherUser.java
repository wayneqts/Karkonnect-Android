package com.appmonarchy.karkonnex.activities;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityOtherUserBinding;
import com.appmonarchy.karkonnex.databinding.PopupSendMessBinding;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUser extends BaseActivity {
    ActivityOtherUserBinding binding;
    String userId = "", blockId = "", email = "";
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.btBlock.setOnClickListener(v -> popupBlock());
        binding.btMess.setOnClickListener(v -> popupSendMess());
        binding.btEmail.setOnClickListener(v -> {
            String mailto = "mailto:"+email;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            startActivity(emailIntent);
        });
        binding.btOpt.setOnClickListener(v -> popupReport());
    }

    // init UI
    private void init(){
        tool.disableBt(binding.btBlock);
        getPf();
    }

    // popup send message
    private void popupSendMess(){
        dialog = new Dialog(this);
        PopupSendMessBinding sendMessBinding = PopupSendMessBinding.inflate(getLayoutInflater());
        dialog.setContentView(sendMessBinding.getRoot());
        tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.WRAP_CONTENT);

        sendMessBinding.tvAsk.setText("Are you sure to send Message to "+binding.tvName.getText().toString()+"?");
        sendMessBinding.btSubmit.setOnClickListener(v -> {
            String mess = sendMessBinding.etMess.getText().toString().trim();
            if (TextUtils.isEmpty(mess)){
                Toast.makeText(this, getString(R.string.message_is_required), Toast.LENGTH_SHORT).show();
            }else {
                sendMess(mess);
            }
        });

        dialog.show();
    }

    // popup block
    private void popupBlock(){
        String method = binding.tvBlock.getText().toString().toLowerCase();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to "+method+" this user?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (method.equals("block")){
                        blockUser();
                        binding.tvBlock.setText(getString(R.string.unblock));
                    }else {
                        unBlock();
                        binding.tvBlock.setText(getString(R.string.block));
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // get profile
    private void getPf(){
        tool.showLoading();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", getIntent().getStringExtra("id"));
        api.postData("view_profile.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        JSONObject jsData = jsonObject.optJSONObject("data");
                        if (jsonObject.optBoolean("status")){
                            if (jsData != null){
                                Glide.with(OtherUser.this).load(jsData.optString("img")).error(R.drawable.no_avatar).centerCrop().into(binding.iv);
                                binding.tvName.setText(jsData.optString("fname")+" "+jsData.optString("lname"));
                                binding.tvAddress.setText(jsData.optString("address"));
                                if (jsData.optString("status").equals("0")){
                                    binding.tvUsername.setText(jsData.optString("username")+" - "+getString(R.string.private_owner));
                                }else {
                                    binding.tvUsername.setText(jsData.optString("username")+" - "+getString(R.string.company_dealer));
                                }
                                binding.tvCt.setText(jsData.optString("country"));
                                binding.tvState.setText(jsData.optString("state"));
                                userId = jsData.optString("id");
                                email = jsData.optString("email");
                                checkBlock();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.llInfo.setVisibility(View.VISIBLE);
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(OtherUser.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // check block list
    private void checkBlock(){
        api.getDataById("block_list.php", pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrData = jsObj.optJSONArray("data");
                        if (arrData != null && arrData.length() > 0){
                            for (int i = 0;i < arrData.length();i++){
                                JSONObject objData = (JSONObject) arrData.get(i);
                                if (objData.optString("blocked").equals(userId)){
                                    binding.tvBlock.setText(getString(R.string.unblock));
                                    blockId = objData.optString("id");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.pbLoading.setVisibility(View.GONE);
                binding.llBt.setVisibility(View.VISIBLE);
                tool.enableBt(binding.btBlock);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // block user
    private void blockUser(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("blockby", pref.getUserId());
        builder.addFormDataPart("blocked", userId);
        api.postData("addblock.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // unblock user
    private void unBlock(){
        api.getDataById("remove_block.php", blockId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // send message
    private void sendMess(String mess){
        tool.showLoading();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("sid", pref.getUserId()).addFormDataPart("rid", userId);
        builder.addFormDataPart("message", mess);
        api.postData("addmsg.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                tool.hideLoading();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                dialog.dismiss();
            }
        });
    }
}