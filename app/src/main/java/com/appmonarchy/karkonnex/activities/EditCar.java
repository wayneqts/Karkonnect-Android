package com.appmonarchy.karkonnex.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.adapter.CustomSpinnerAdapter;
import com.appmonarchy.karkonnex.databinding.ActivityEditCarBinding;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.appmonarchy.karkonnex.model.Goal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCar extends BaseActivity {
    ActivityEditCarBinding binding;
    List<Goal> stateList, countryList;
    String stateId = "", countryId = "", type = "0";
    List<Bitmap> imgList;
    int imgPos, SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_rent) {
                type = "0";
                binding.llRent.setVisibility(View.VISIBLE);
                binding.llPrice.setVisibility(View.GONE);
            } else {
                type = "1";
                binding.llRent.setVisibility(View.GONE);
                binding.llPrice.setVisibility(View.VISIBLE);
            }
        });
        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(getColor(R.color.grey1));
                }else {
                    stateId = stateList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(getColor(R.color.grey1));
                } else {
                    countryId = countryList.get(position).getId();
                    getState(countryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btSelect1.setOnClickListener(v -> {
            imgPos = 0;
            popupSelectPhoto();
        });
        binding.btSelect2.setOnClickListener(v -> {
            imgPos = 1;
            popupSelectPhoto();
        });
        binding.btSelect3.setOnClickListener(v -> {
            imgPos = 2;
            popupSelectPhoto();
        });
        binding.btSelect4.setOnClickListener(v -> {
            imgPos = 3;
            popupSelectPhoto();
        });
        binding.btSave.setOnClickListener(v -> {
            String make = binding.etMake.getText().toString().trim();
            String model = binding.etModel.getText().toString().trim();
            String year = binding.etYear.getText().toString().trim();
            String day = binding.etDay.getText().toString().trim();
            String week = binding.etWeek.getText().toString().trim();
            String month = binding.etMonth.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String city = binding.etCity.getText().toString().trim();
            String code = binding.etZipCode.getText().toString().trim();
            String des = binding.etDes.getText().toString().trim();
            String price = binding.etPrice.getText().toString().trim();
            if (TextUtils.isEmpty(make)){
                Toast.makeText(this, getString(R.string.make_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(model)){
                Toast.makeText(this, getString(R.string.model_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(year)){
                Toast.makeText(this, getString(R.string.year_is_required), Toast.LENGTH_SHORT).show();
            }else if (type.equals("0") && TextUtils.isEmpty(day)){
                Toast.makeText(this, getString(R.string.rent_day_is_required), Toast.LENGTH_SHORT).show();
            }else if (type.equals("0") && TextUtils.isEmpty(week)){
                Toast.makeText(this, getString(R.string.rent_week_is_required), Toast.LENGTH_SHORT).show();
            }else if (type.equals("0") && TextUtils.isEmpty(month)){
                Toast.makeText(this, getString(R.string.rent_month_is_required), Toast.LENGTH_SHORT).show();
            }else if (type.equals("1") && TextUtils.isEmpty(price)){
                Toast.makeText(this, getString(R.string.price_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(address)){
                Toast.makeText(this, getString(R.string.location_address_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(city)){
                Toast.makeText(this, getString(R.string.city_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(code)){
                Toast.makeText(this, getString(R.string.zip_code_is_required), Toast.LENGTH_SHORT).show();
            }else if (countryId.equals("")){
                Toast.makeText(this, getString(R.string.country_is_required), Toast.LENGTH_SHORT).show();
            }else if (stateId.equals("")){
                Toast.makeText(this, getString(R.string.state_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(des)){
                Toast.makeText(this, getString(R.string.description_is_required), Toast.LENGTH_SHORT).show();
            }else {
                editCar();
            }
        });
    }

    // init UI
    private void init(){
        imgList = new ArrayList<>();
        CarInfo carInfo = (CarInfo) getIntent().getSerializableExtra("data");
        type = carInfo.getType();
        if (type.equals("") || type.equals("1")){
            binding.llRent.setVisibility(View.GONE);
            binding.llPrice.setVisibility(View.VISIBLE);
            binding.rbSell.setChecked(true);
        }else {
            binding.llRent.setVisibility(View.VISIBLE);
            binding.llPrice.setVisibility(View.GONE);
            binding.rbRent.setChecked(true);
        }
        binding.etPrice.setText(carInfo.getPriceDay());
        binding.etMake.setText(carInfo.getMake());
        binding.etModel.setText(carInfo.getModal());
        binding.etYear.setText(carInfo.getYear());
        binding.etDay.setText(carInfo.getPriceDay());
        binding.etWeek.setText(carInfo.getPriceWeek());
        binding.etMonth.setText(carInfo.getPriceMonth());
        binding.etAddress.setText(carInfo.getAddress());
        binding.etCity.setText(carInfo.getCity());
        binding.etZipCode.setText(carInfo.getZip());
        binding.etDes.setText(carInfo.getDes());

        Glide.with(this).load(carInfo.getImg1()).error(R.drawable.img_no_car).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> imgList.add(tool.imgToBm(binding.ivImg1)), 1000);
                return false;
            }
        }).into(binding.ivImg1);
        Glide.with(this).load(carInfo.getImg2()).error(R.drawable.img_no_car).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> imgList.add(tool.imgToBm(binding.ivImg2)), 1000);
                return false;
            }
        }).into(binding.ivImg2);
        Glide.with(this).load(carInfo.getImg3()).error(R.drawable.img_no_car).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> imgList.add(tool.imgToBm(binding.ivImg3)), 1000);
                return false;
            }
        }).into(binding.ivImg3);
        Glide.with(this).load(carInfo.getImg4()).error(R.drawable.img_no_car).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> imgList.add(tool.imgToBm(binding.ivImg4)), 1000);
                return false;
            }
        }).into(binding.ivImg4);
        stateId = carInfo.getState();
        countryId = carInfo.getCountry();
        getCountry();
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
                int size = imgList.size();
                switch (imgPos){
                    case 0:
                        if (size > 0){
                            imgList.remove(imgPos);
                            imgList.add(imgPos, bm);
                        }else {
                            imgList.add(imgPos, bm);
                        }
                        Glide.with(this).load(bm).centerCrop().into(binding.ivImg1);
                        break;
                    case 1:
                        if (size > 1){
                            imgList.remove(imgPos);
                            imgList.add(imgPos, bm);
                        }else {
                            imgList.add(imgPos, bm);
                        }
                        Glide.with(this).load(bm).centerCrop().into(binding.ivImg2);
                        break;
                    case 2:
                        if (size > 2){
                            imgList.remove(imgPos);
                            imgList.add(imgPos, bm);
                        }else {
                            imgList.add(imgPos, bm);
                        }
                        Glide.with(this).load(bm).centerCrop().into(binding.ivImg3);
                        break;
                    case 3:
                        if (size > 3){
                            imgList.remove(imgPos);
                            imgList.add(imgPos, bm);
                        }else {
                            imgList.add(imgPos, bm);
                        }
                        Glide.with(this).load(bm).centerCrop().into(binding.ivImg4);
                        break;
                }
            }
            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    int size = imgList.size();
                    switch (imgPos){
                        case 0:
                            if (size > 0){
                                imgList.remove(imgPos);
                                imgList.add(imgPos, bm);
                            }else {
                                imgList.add(imgPos, bm);
                            }
                            Glide.with(this).load(bm).centerCrop().into(binding.ivImg1);
                            break;
                        case 1:
                            if (size > 1){
                                imgList.remove(imgPos);
                                imgList.add(imgPos, bm);
                            }else {
                                imgList.add(imgPos, bm);
                            }
                            Glide.with(this).load(bm).centerCrop().into(binding.ivImg2);
                            break;
                        case 2:
                            if (size > 2){
                                imgList.remove(imgPos);
                                imgList.add(imgPos, bm);
                            }else {
                                imgList.add(imgPos, bm);
                            }
                            Glide.with(this).load(bm).centerCrop().into(binding.ivImg3);
                            break;
                        case 3:
                            if (size > 3){
                                imgList.remove(imgPos);
                                imgList.add(imgPos, bm);
                            }else {
                                imgList.add(imgPos, bm);
                            }
                            Glide.with(this).load(bm).centerCrop().into(binding.ivImg4);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // edit car
    private void editCar(){
        tool.showLoading();
        String make = binding.etMake.getText().toString().trim();
        String model = binding.etModel.getText().toString().trim();
        String year = binding.etYear.getText().toString().trim();
        String day = binding.etDay.getText().toString().trim();
        String week = binding.etWeek.getText().toString().trim();
        String month = binding.etMonth.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String code = binding.etZipCode.getText().toString().trim();
        String des = binding.etDes.getText().toString().trim();
        String price = binding.etPrice.getText().toString().trim();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", pref.getUserId()).addFormDataPart("type", type).
                addFormDataPart("make", make).addFormDataPart("modal", model).
                addFormDataPart("year", year).
                addFormDataPart("address", address).addFormDataPart("city", city).
                addFormDataPart("zip", code).addFormDataPart("country", countryId).
                addFormDataPart("state", stateId).addFormDataPart("discription", des);
        if (type.equals("0")){
            builder.addFormDataPart("rent", day).addFormDataPart("rentw", week).
                    addFormDataPart("rentm", month);
        }else {
            builder.addFormDataPart("rent", price);
        }
        if (imgList.size() == 1) {
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(0)));
            builder.addFormDataPart("limg1", tool.getFileFromBm(imgList.get(0)).getName(), rqFile);
        } else if (imgList.size() == 2) {
            RequestBody rqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(0)));
            RequestBody rqFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(1)));
            builder.addFormDataPart("limg1", tool.getFileFromBm(imgList.get(0)).getName(), rqFile1);
            builder.addFormDataPart("limg2", tool.getFileFromBm(imgList.get(1)).getName(), rqFile2);
        } else if (imgList.size() == 3){
            RequestBody rqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(0)));
            RequestBody rqFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(1)));
            RequestBody rqFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(2)));
            builder.addFormDataPart("limg1", tool.getFileFromBm(imgList.get(0)).getName(), rqFile1);
            builder.addFormDataPart("limg2", tool.getFileFromBm(imgList.get(1)).getName(), rqFile2);
            builder.addFormDataPart("limg3", tool.getFileFromBm(imgList.get(2)).getName(), rqFile3);
        }else {
            RequestBody rqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(0)));
            RequestBody rqFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(1)));
            RequestBody rqFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(2)));
            RequestBody rqFile4 = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgList.get(3)));
            builder.addFormDataPart("limg1", tool.getFileFromBm(imgList.get(0)).getName(), rqFile1);
            builder.addFormDataPart("limg2", tool.getFileFromBm(imgList.get(1)).getName(), rqFile2);
            builder.addFormDataPart("limg3", tool.getFileFromBm(imgList.get(2)).getName(), rqFile3);
            builder.addFormDataPart("limg4", tool.getFileFromBm(imgList.get(3)).getName(), rqFile4);
        }
        api.postData("edit_car.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            setResult(AppConstrains.REFRESH_CAR_CODE);
                            finish();
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
                Toast.makeText(EditCar.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get country
    private void getCountry() {
        api.getDataArr("countries.php").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                int pos = 0;
                String[] countryArr = null;
                countryList = new ArrayList<>();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        countryList.add(new Goal("0", "Country"));
                        countryArr = new String[jsArr.length() + 1];
                        countryArr[0] = "Country";
                        for (int i = 0; i < jsArr.length(); i++) {
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            countryList.add(new Goal(obj.optString("id"), obj.optString("country_name")));
                            countryArr[i + 1] = obj.optString("country_name");
                            if (obj.optString("id").equals(countryId)){
                                pos = i + 1;
                                getState(countryId);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (countryArr != null) {
                    CustomSpinnerAdapter countryAdapter = new CustomSpinnerAdapter(EditCar.this, android.R.layout.simple_spinner_item, countryArr);
                    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCountry.setAdapter(countryAdapter);
                    binding.spinnerCountry.setSelection(pos);
                }
                binding.pbCt.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(EditCar.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get state
    private void getState(String id) {
        binding.spinnerState.setVisibility(View.GONE);
        binding.ivDown.setVisibility(View.GONE);
        binding.pbState.setVisibility(View.VISIBLE);
        api.getArrById("state.php", id).enqueue(new Callback<JsonArray>() {
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
                    CustomSpinnerAdapter stateAdapter = new CustomSpinnerAdapter(EditCar.this, android.R.layout.simple_spinner_item, stateArr);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerState.setAdapter(stateAdapter);
                }
                binding.spinnerState.setVisibility(View.VISIBLE);
                binding.ivDown.setVisibility(View.VISIBLE);
                binding.pbState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(EditCar.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}