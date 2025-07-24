package com.appmonarchy.karkonnex.activities.authentication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.adapter.CustomSpinnerAdapter;
import com.appmonarchy.karkonnex.databinding.ActivitySignupBinding;
import com.appmonarchy.karkonnex.model.Goal;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends BaseActivity {
    ActivitySignupBinding binding;
    int SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0;
    List<Goal> stateList;
    String phoneStt = "1", stt = "0", stateId = "";
    Bitmap bmImg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.btSignup.setOnClickListener(v -> {
            String fName = binding.etFName.getText().toString().trim();
            String lName = binding.etLName.getText().toString().trim();
            String company = binding.etCompany.getText().toString().trim();
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String pw = binding.etPw.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String city = binding.etCity.getText().toString().trim();
            String zipcode = binding.etZipCode.getText().toString().trim();
            if (TextUtils.isEmpty(fName)){
                Toast.makeText(this, getString(R.string.first_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(lName)) {
                Toast.makeText(this, getString(R.string.last_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (stt.equals("1") && TextUtils.isEmpty(company)){
                Toast.makeText(this, getString(R.string.company_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(username)){
                Toast.makeText(this, getString(R.string.username_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(pw)){
                Toast.makeText(this, getString(R.string.password_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, getString(R.string.telephone_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(address)){
                Toast.makeText(this, getString(R.string.location_address_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(city)){
                Toast.makeText(this, getString(R.string.city_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(zipcode)){
                Toast.makeText(this, getString(R.string.zip_code_is_required), Toast.LENGTH_SHORT).show();
            }else if (stateId.equals("")){
                Toast.makeText(this, getString(R.string.state_is_required), Toast.LENGTH_SHORT).show();
            }else if (bmImg == null){
                Toast.makeText(this, getString(R.string.profile_photo_is_required), Toast.LENGTH_SHORT).show();
            }else {
                signUp();
            }
        });
        binding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_private) {
                stt = "0";
            } else {
                stt = "1";
            }
        });
        binding.rgShow.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_visible) {
                phoneStt = "1";
            } else {
                phoneStt = "0";
            }
        });
        binding.btChooseFile.setOnClickListener(v -> popupSelectPhoto());
        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(getColor(R.color.grey1));
                } else {
                    stateId = stateList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // init UI
    private void init(){
        getState();
    }

    // popup select photo
    public void popupSelectPhoto() {
        CharSequence[] items = {"Take a photo", "Choose from library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take a photo")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    cameraIntent();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        galleryIntent();
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        galleryIntent();
                    }
                }
            }
        });
        builder.show();
    }

    // request permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    galleryIntent();
                } else {
                    Toast.makeText(this, getString(R.string.permission_access_device_image_denied), Toast.LENGTH_SHORT).show();
                }
            });

    // select image from library
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(this, getString(R.string.permission_access_device_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                Toast.makeText(this, getString(R.string.permission_access_camera_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_CODE) {
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                bmImg = bm;
                binding.rlImg.setVisibility(View.VISIBLE);
                binding.ivImg.setImageBitmap(bm);
                binding.btChooseFile.setVisibility(View.GONE);
            }
            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    bmImg = bm;
                    binding.ivImg.setImageBitmap(bm);
                    binding.rlImg.setVisibility(View.VISIBLE);
                    binding.btChooseFile.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // sign up
    private void signUp(){
        tool.showLoading();
        String fName = binding.etFName.getText().toString().trim();
        String lName = binding.etLName.getText().toString().trim();
        String company = binding.etCompany.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String pw = binding.etPw.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String zipcode = binding.etZipCode.getText().toString().trim();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("status", stt).addFormDataPart("fname", fName).addFormDataPart("lname", lName).
                addFormDataPart("company", company).addFormDataPart("username", username).addFormDataPart("email", email).
                addFormDataPart("password", pw).addFormDataPart("phone", phone).addFormDataPart("phone_status", phoneStt).
                addFormDataPart("address", address).addFormDataPart("city", city).addFormDataPart("zip", zipcode).
                addFormDataPart("country", "231").addFormDataPart("state", stateId);
        RequestBody rqFile = RequestBody.create(tool.getFileFromBm(bmImg), MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("img", tool.getFileFromBm(bmImg).getName(), rqFile);
        api.register(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")) {
                            startActivity(new Intent(SignUp.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            pref.setUserId(jsonObject.optString("message"));
                        } else {
                            Toast.makeText(SignUp.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SignUp.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get state
    private void getState() {
        api.getArrById("state.php", "231").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String[] stateArr = null;
                stateList = new ArrayList<>();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        stateList.add(new Goal("0", "State"));
                        stateArr = new String[jsArr.length() + 1];
                        stateArr[0] = "State/Province";
                        for (int i = 0; i < jsArr.length(); i++) {
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            stateList.add(new Goal(obj.optString("id"), obj.optString("name")));
                            stateArr[i + 1] = obj.optString("name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (stateArr != null) {
                    CustomSpinnerAdapter stateAdapter = new CustomSpinnerAdapter(SignUp.this, android.R.layout.simple_spinner_item, stateArr);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerState.setAdapter(stateAdapter);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SignUp.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}