package com.appmonarchy.karkonnex.activities.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.databinding.ActivityForgotPwBinding;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPw extends BaseActivity {
    ActivityForgotPwBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
        binding.btSubmit.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else{
                resetPw();
            }
        });
    }

    // reset password
    private void resetPw(){
        tool.showLoading();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("email", binding.etEmail.getText().toString().trim());
        api.resetPw(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (jsObj.optBoolean("Status")){
                            Toast.makeText(ForgotPw.this, jsObj.optString("Message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ForgotPw.this, jsObj.optString("Message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ForgotPw.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}