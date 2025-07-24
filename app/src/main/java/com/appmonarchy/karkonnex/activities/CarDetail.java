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

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityCarDetailBinding;
import com.appmonarchy.karkonnex.databinding.PopupSendMessBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetail extends BaseActivity {
    ActivityCarDetailBinding binding;
    String email, phone, userId, name, rentD = "", rentW = "", rentM = "", type = "";
    Dialog dialog;
    List<String> imgList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (new Date().after(tool.stringToDate("15/12/2024"))){
            binding.btBook.setVisibility(View.VISIBLE);
        }
        binding.btBack.setOnClickListener(v -> finish());
        binding.btOpt.setOnClickListener(v -> popupReport());
        binding.btCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phone));
            startActivity(intent);
        });
        binding.btEmail.setOnClickListener(v -> {
            String mailto = "mailto:"+email;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            startActivity(emailIntent);
        });
        binding.btMess.setOnClickListener(v -> popupSendMess());
        binding.btBook.setOnClickListener(v -> startActivity(new Intent(this, Booking.class).putExtra("day", rentD)
                .putExtra("week", rentW).putExtra("month", rentM).putExtra("pid", binding.tvPid.getText().toString())
                .putExtra("type", type)));
        binding.img1.setOnClickListener(v -> new StfalconImageViewer.Builder<>(this, imgList, (imageView1, image) ->
                Glide.with(this).load(image).error(R.drawable.img_no_car).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(imageView1)).withStartPosition(0).show());
        binding.img2.setOnClickListener(v -> new StfalconImageViewer.Builder<>(this, imgList, (imageView1, image) ->
                Glide.with(this).load(image).error(R.drawable.img_no_car).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(imageView1)).withStartPosition(1).show());
        binding.img3.setOnClickListener(v -> new StfalconImageViewer.Builder<>(this, imgList, (imageView1, image) ->
                Glide.with(this).load(image).error(R.drawable.img_no_car).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(imageView1)).withStartPosition(2).show());
        binding.img4.setOnClickListener(v -> new StfalconImageViewer.Builder<>(this, imgList, (imageView1, image) ->
                Glide.with(this).load(image).error(R.drawable.img_no_car).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(imageView1)).withStartPosition(3).show());

        getData();
    }

    // popup send message
    private void popupSendMess(){
        dialog = new Dialog(this);
        PopupSendMessBinding sendMessBinding = PopupSendMessBinding.inflate(getLayoutInflater());
        dialog.setContentView(sendMessBinding.getRoot());
        tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.WRAP_CONTENT);

        sendMessBinding.tvAsk.setText("Are you sure to send Message to "+name+"?");
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

    // get data
    private void getData(){
        imgList = new ArrayList<>();
        tool.showLoading();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", getIntent().getStringExtra("id"));
        api.postData("view_car.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        JSONArray jsData = jsonObject.optJSONArray("data");
                        if (jsData != null && jsData.length() > 0){
                            for (int i = 0;i < jsData.length();i++){
                                Object json = new JSONTokener(jsData.get(i).toString()).nextValue();
                                if (json instanceof JSONObject) {
                                    JSONObject jsObj = (JSONObject) jsData.get(0);
                                    JSONObject jsCar = jsObj.optJSONObject("car");
                                    binding.tvCarName.setText(jsCar.optString("make") + " " + jsCar.optString("modal") + " " + jsCar.optString("year"));
                                    type = jsCar.optString("type");
                                    if (type.equals("") || type.equals("1")) {
                                        binding.tvType.setText(getString(R.string.for_sale));
                                    } else {
                                        binding.tvType.setText(getString(R.string.for_rent));
                                    }
                                    Glide.with(CarDetail.this).load(jsCar.optString("img1")).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.img1);
                                    Glide.with(CarDetail.this).load(jsCar.optString("img2")).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.img2);
                                    Glide.with(CarDetail.this).load(jsCar.optString("img3")).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.img3);
                                    Glide.with(CarDetail.this).load(jsCar.optString("img4")).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.img4);
                                    imgList.add(jsCar.optString("img1"));
                                    imgList.add(jsCar.optString("img2"));
                                    imgList.add(jsCar.optString("img3"));
                                    imgList.add(jsCar.optString("img4"));
                                    rentD = jsCar.optString("rent");
                                    rentW = jsCar.optString("rentw");
                                    rentM = jsCar.optString("rentm");
                                    binding.tvPriceDay.setText("$" + jsCar.optString("rent") + "/Per Day");
                                    binding.tvPriceWeek.setText("$" + jsCar.optString("rentw") + "/Weekly");
                                    binding.tvPriceMonth.setText("$" + jsCar.optString("rentm") + "/Month");
                                    binding.tvDes.setText(jsCar.optString("discription"));
                                    binding.tvAddress.setText(jsCar.optString("address"));
                                    binding.tvPid.setText(jsCar.optString("pid"));
                                    userId = jsCar.optString("uid");
                                    getPf(jsCar.optString("uid"));
                                }else {
                                    binding.rlMain.setVisibility(View.VISIBLE);
                                    binding.tvNoData.setVisibility(View.VISIBLE);
                                    binding.nsv.setVisibility(View.GONE);
                                    binding.btOpt.setVisibility(View.GONE);
                                    tool.hideLoading();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(CarDetail.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get profile
    private void getPf(String id){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", id);
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
                                phone = jsData.optString("phone");
                                email = jsData.optString("email");
                                name = jsData.optString("fname")+" "+jsData.optString("lname");
                                Glide.with(CarDetail.this).load(jsData.optString("img")).error(R.drawable.no_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.ivUser);
                                if (jsData.optString("status").equals("0")){
                                    binding.tvUsername.setText(jsData.optString("username")+" - "+getString(R.string.private_owner));
                                }else {
                                    binding.tvUsername.setText(jsData.optString("username")+" - "+getString(R.string.company_dealer));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.rlMain.setVisibility(View.VISIBLE);
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(CarDetail.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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