package com.appmonarchy.karkonnex.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.adapter.ChatAdapter;
import com.appmonarchy.karkonnex.databinding.ActivityUserChatBinding;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.Chat;
import com.appmonarchy.karkonnex.model.Messages;
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

public class UserChat extends BaseActivity {
    ActivityUserChatBinding binding;
    List<Chat> list;
    ChatAdapter adapter;
    Messages mess;
    boolean sentMess = false;
    int SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0;
    Bitmap imgBm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> {
            if (sentMess){
                setResult(AppConstrains.REFRESH_MESS_CODE);
            }
            finish();
        });
        binding.etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0){
                    tool.enableBt(binding.btSend);
                }else {
                    tool.disableBt(binding.btSend);
                }
            }
        });
        binding.btSend.setOnClickListener(v -> {
            binding.btSend.setVisibility(View.INVISIBLE);
            binding.pbLoading.setVisibility(View.VISIBLE);
            tool.hideKeyboard(v);
            binding.etChat.clearFocus();
            sendMess("mess");
        });
        binding.btImg.setOnClickListener(v -> {
            binding.etChat.clearFocus();
            popupSelectPhoto();
        });
    }

    // init UI
    private void init(){
        tool.disableBt(binding.btSend);
        mess = (Messages) getIntent().getSerializableExtra("data");
        binding.tvUsername.setText(mess.getUserName());
        list = new ArrayList<>();
        adapter = new ChatAdapter(list, this);
        binding.rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rcv.setAdapter(adapter);
        tool.rcvNoAnimator(binding.rcv);
        tool.showLoading();
        getData();
    }

    // popup select photo
    public void popupSelectPhoto(){
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
        if (requestCode == REQUEST_CAMERA){
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
        if(resultCode == RESULT_OK && data!=null){
            if (requestCode == CAMERA_CODE){
                imgBm = (Bitmap) data.getExtras().get("data");
                binding.btSend.setVisibility(View.INVISIBLE);
                binding.pbLoading.setVisibility(View.VISIBLE);
                sendMess("media");
            }
            if (requestCode == SELECT_FILE){
                Uri uri = data.getData();
                try {
                    imgBm = MediaStore.Images.Media.getBitmap(this. getContentResolver(), uri);
                    binding.btSend.setVisibility(View.INVISIBLE);
                    binding.pbLoading.setVisibility(View.VISIBLE);
                    sendMess("media");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // get data
    private void getData(){
        api.getMessList(mess.getsId(), mess.getrId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                list.clear();
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        JSONArray jsArr = jsObj.optJSONArray("Data");
                        if (jsArr != null && jsArr.length() > 0){
                            for (int i = 0;i < jsArr.length();i++){
                                JSONObject objData = (JSONObject) jsArr.get(i);
                                list.add(new Chat(objData.optString("sender_id"), objData.optString("created_at"), objData.optString("message"),
                                        objData.optString("readstatus"), objData.optString("media")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                tool.hideLoading();
                binding.pbLoading.setVisibility(View.GONE);
                binding.btSend.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> binding.nsv.smoothScrollTo(0, binding.nsv.getChildAt(0).getHeight()), 100);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(UserChat.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // send message
    private void sendMess(String type){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("sid", mess.getsId()).addFormDataPart("rid", mess.getrId());
        if (type.equals("mess")){
            builder.addFormDataPart("message", binding.etChat.getText().toString().trim());
        }else {
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(imgBm));
            builder.addFormDataPart("media", tool.getFileFromBm(imgBm).getName(), rqFile);
        }
        api.postData("addmsg.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    getData();
                    sentMess = true;
                    if (type.equals("mess")){
                        binding.etChat.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}