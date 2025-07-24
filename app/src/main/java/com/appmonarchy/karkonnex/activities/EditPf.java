package com.appmonarchy.karkonnex.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityEditPfBinding;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.Profile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPf extends BaseActivity {
    ActivityEditPfBinding binding;
    boolean updatedPf = false;
    String phoneStt, stt;
    Bitmap bmImg;
    int SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> {
            if (updatedPf){
                setResult(AppConstrains.REFRESH_PF_CODE);
            }
            finish();
        });
        binding.btChooseImg.setOnClickListener(v -> popupSelectPhoto());
        binding.btSave.setOnClickListener(v -> {
            String fName = binding.etFName.getText().toString().trim();
            String lName = binding.etLName.getText().toString().trim();
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String city = binding.etCity.getText().toString().trim();
            String zipcode = binding.etZipCode.getText().toString().trim();
            if (TextUtils.isEmpty(fName)){
                Toast.makeText(this, getString(R.string.first_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(lName)) {
                Toast.makeText(this, getString(R.string.last_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(username)){
                Toast.makeText(this, getString(R.string.username_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, getString(R.string.telephone_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(address)){
                Toast.makeText(this, getString(R.string.location_address_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(city)){
                Toast.makeText(this, getString(R.string.city_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(zipcode)){
                Toast.makeText(this, getString(R.string.zip_code_is_required), Toast.LENGTH_SHORT).show();
            }else{
                updatePf();
            }
        });
    }

    // init UI
    private void init(){
        Profile pf = (Profile) getIntent().getSerializableExtra("data");
        stt = pf.getStt();
        phoneStt = pf.getPhoneStt();
        if (stt.equals("0")){
            binding.rbPrivate.setChecked(true);
        }else {
            binding.rbCompany.setChecked(true);
        }
        binding.etFName.setText(pf.getfName());
        binding.etLName.setText(pf.getlName());
        binding.etUsername.setText(pf.getUsername());
        binding.etEmail.setText(pf.getEmail());
        binding.etPhone.setText(pf.getPhone());
        binding.etAddress.setText(pf.getAddress());
        binding.etCity.setText(pf.getCity());
        binding.etZipCode.setText(pf.getZip());
        Glide.with(this).load(pf.getImg()).error(R.drawable.no_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> bmImg = tool.imgToBm(binding.ivImg), 1000);
                return false;
            }
        }).into(binding.ivImg);
        if (phoneStt.equals("0")){
            binding.rbHide.setChecked(true);
        }else {
            binding.rbVisible.setChecked(true);
        }
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
                Glide.with(this).load(bm).centerCrop().into(binding.ivImg);
            }
            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    bmImg = bm;
                    Glide.with(this).load(bm).centerCrop().into(binding.ivImg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update profile
    private void updatePf(){
        tool.showLoading();
        String fName = binding.etFName.getText().toString().trim();
        String lName = binding.etLName.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String zipcode = binding.etZipCode.getText().toString().trim();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", pref.getUserId()).addFormDataPart("fname", fName)
                .addFormDataPart("lname", lName).addFormDataPart("username", username).addFormDataPart("email", email)
                .addFormDataPart("phone", phone).addFormDataPart("phone_status", phoneStt).addFormDataPart("status", stt)
                .addFormDataPart("city", city).addFormDataPart("zip", zipcode).addFormDataPart("address", address);
        RequestBody rqFile = RequestBody.create(tool.getFileFromBm(bmImg), MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("img", tool.getFileFromBm(bmImg).getName(), rqFile);
        api.postData("edit_profile.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            Toast.makeText(EditPf.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                            updatedPf = true;
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
                Toast.makeText(EditPf.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}