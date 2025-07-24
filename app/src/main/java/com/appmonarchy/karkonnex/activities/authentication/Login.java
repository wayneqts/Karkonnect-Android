package com.appmonarchy.karkonnex.activities.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.databinding.ActivityLoginBinding;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseActivity {
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.tvToSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUp.class)));
        binding.tvToFg.setOnClickListener(v -> startActivity(new Intent(this, ForgotPw.class)));
        binding.btLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String pw = binding.etPw.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(pw)){
                Toast.makeText(this, getString(R.string.password_is_required), Toast.LENGTH_SHORT).show();
            }else{
                login(email, pw);
            }
        });
    }

    // init UI
    private void init(){
        tool.tvMarkDown(R.string.by_proceeding_to_use_matcheron_you_agree_to_our_terms_and_privacy_policy, binding.tvTerms);
    }

    // login
    private void login(String email, String pw){
        tool.showLoading();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("email", email).addFormDataPart("password", pw);
        api.login(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            pref.setUserId(jsonObject.optString("id"));
                            startActivity(new Intent(Login.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                        }else {
                            Toast.makeText(Login.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(Login.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}