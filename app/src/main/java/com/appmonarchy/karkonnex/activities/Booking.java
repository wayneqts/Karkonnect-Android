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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.databinding.ActivityBookingBinding;
import com.appmonarchy.karkonnex.model.Goal;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Booking extends BaseActivity {
    ActivityBookingBinding binding;
    int SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0, selectedDay, selectedMonth, selectedYear, rentD, rentW, rentM;
    Bitmap bmImg = null;
    String rentType = "day";
    boolean isSlash = false;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.btChooseFile.setOnClickListener(v -> popupSelectPhoto());
        binding.etFrom.setOnClickListener(v -> {
            String till = binding.etTill.getText().toString();
            popupDatePicker("from", till);
        });
        binding.etTill.setOnClickListener(v -> {
            String from = binding.etFrom.getText().toString();
            popupDatePicker("till", from);
        });
        binding.rgPrice.setOnCheckedChangeListener((group, checkedId) -> {
            binding.etTill.setText("");
            binding.etNum.setText("");
            binding.etAmount.setText("");
            if (checkedId == R.id.rb_day) {
                binding.llTill.setVisibility(View.VISIBLE);
                binding.llNum.setVisibility(View.GONE);
                rentType = "day";
                if (type.equals("1")){
                    binding.etAmount.setText(String.valueOf(rentD));
                }
            } else {
                binding.llTill.setVisibility(View.GONE);
                binding.llNum.setVisibility(View.VISIBLE);
                if (checkedId == R.id.rb_week) {
                    rentType = "week";
                    binding.tvTitleEt.setText(getString(R.string.enter_number_of_week));
                    if (type.equals("1")){
                        binding.etAmount.setText(String.valueOf(rentW));
                    }
                } else {
                    rentType = "month";
                    binding.tvTitleEt.setText(getString(R.string.enter_number_of_month));
                    if (type.equals("1")){
                        binding.etAmount.setText(String.valueOf(rentM));
                    }
                }
            }
        });
        binding.etNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String num = s.toString().trim();
                if (num.length() > 0) {
                    if (rentType.equals("week")) {
                        binding.etAmount.setText(String.valueOf(Integer.parseInt(num) * rentW));
                    } else {
                        binding.etAmount.setText(String.valueOf(Integer.parseInt(num) * rentM));
                    }
                } else {
                    binding.etAmount.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etExpiration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    formatCardExpiringDate(s);
                } catch (NumberFormatException e) {
                    s.clear();
                }
            }
        });
        binding.etCardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int pos = 0;
                while (pos < s.length()) {
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }
                pos = 4;
                while (pos < s.length()) {
                    final char c = s.charAt(pos);
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }
            }
        });
        binding.btBook.setOnClickListener(v -> {
            String from = binding.etFrom.getText().toString().trim();
            String till = binding.etTill.getText().toString().trim();
            String num = binding.etNum.getText().toString().trim();
            String bgCheck = binding.etBg.getText().toString().trim();
            String license = binding.etLicense.getText().toString().trim();
            String cardNo = binding.etCardNo.getText().toString().trim();
            String cardName = binding.etCardName.getText().toString().trim();
            String exp = binding.etExpiration.getText().toString().trim();
            String cvv = binding.etCvv.getText().toString().trim();
            String zip = binding.etZip.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(from)){
                Toast.makeText(this, getString(R.string.from_is_required), Toast.LENGTH_SHORT).show();
            }else if (rentType.equals("day") && TextUtils.isEmpty(till)){
                Toast.makeText(this, getString(R.string.till_is_required), Toast.LENGTH_SHORT).show();
            }else if (rentType.equals("week") && TextUtils.isEmpty(num)) {
                Toast.makeText(this, getString(R.string.number_of_week_is_required), Toast.LENGTH_SHORT).show();
            }else if (rentType.equals("month") && TextUtils.isEmpty(num)){
                    Toast.makeText(this, getString(R.string.number_of_month_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(bgCheck)){
                Toast.makeText(this, getString(R.string.background_check_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(license)){
                Toast.makeText(this, getString(R.string.driving_license_is_required), Toast.LENGTH_SHORT).show();
            }else if (bmImg == null){
                Toast.makeText(this, getString(R.string.driving_license_photo_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(cardNo)){
                Toast.makeText(this, getString(R.string.card_number_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(cardName)){
                Toast.makeText(this, getString(R.string.card_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(exp)){
                Toast.makeText(this, getString(R.string.expiration_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(cvv)){
                Toast.makeText(this, getString(R.string.cvv_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(zip)){
                Toast.makeText(this, getString(R.string.zip_code_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, getString(R.string.phone_is_required), Toast.LENGTH_SHORT).show();
            }else {
                booking();
            }
        });
    }

    // init UI
    private void init() {
        Calendar c = Calendar.getInstance();
        selectedDay = c.get(Calendar.DAY_OF_MONTH);
        selectedMonth = c.get(Calendar.MONTH);
        selectedYear = c.get(Calendar.YEAR);
        if (!getIntent().getStringExtra("day").equals("")){
            rentD = Integer.parseInt(getIntent().getStringExtra("day"));
        }else {
            rentD = 0;
        }
        if (!getIntent().getStringExtra("week").equals("")){
            rentW = Integer.parseInt(getIntent().getStringExtra("week"));
        }else {
            rentW = 0;
        }
        if (!getIntent().getStringExtra("month").equals("")){
            rentM = Integer.parseInt(getIntent().getStringExtra("month"));
        }else {
            rentM = 0;
        }
        type = getIntent().getStringExtra("type");
        if (type.equals("") || type.equals("1")){
            binding.etAmount.setText(String.valueOf(rentD));
            binding.llRent.setVisibility(View.GONE);
        }
        binding.rbDay.setText("$" + rentD + "/Day");
        binding.rbWeek.setText("$" + rentW + "/Week");
        binding.rbMonth.setText("$" + rentM + "/Month");
    }

    private void formatCardExpiringDate(Editable s) {
        String input = s.toString();
        String mLastInput = "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
        Calendar expiryDateDate = Calendar.getInstance();
        try {
            expiryDateDate.setTime(formatter.parse(input));
        } catch (java.text.ParseException e) {
            if (s.length() == 2 && !mLastInput.endsWith("/") && isSlash) {
                isSlash = false;
                int month = Integer.parseInt(input);
                if (month <= 12) {
                    binding.etExpiration.setText(binding.etExpiration.getText().toString().substring(0, 1));
                    binding.etExpiration.setSelection(binding.etExpiration.getText().toString().length());
                } else {
                    s.clear();
                    binding.etExpiration.setText("");
                    binding.etExpiration.setSelection(binding.etExpiration.getText().toString().length());
                }
            } else if (s.length() == 2 && !mLastInput.endsWith("/") && !isSlash) {
                isSlash = true;
                int month = Integer.parseInt(input);
                if (month <= 12) {
                    binding.etExpiration.setText(binding.etExpiration.getText().toString() + "/");
                    binding.etExpiration.setSelection(binding.etExpiration.getText().toString().length());
                } else {
                    binding.etExpiration.setText("");
                    binding.etExpiration.setSelection(binding.etExpiration.getText().toString().length());
                    s.clear();
                }
            } else if (s.length() == 1) {
                int month = Integer.parseInt(input);
                if (month > 1 && month < 12) {
                    isSlash = true;
                    binding.etExpiration.setText("0" + binding.etExpiration.getText().toString() + "/");
                    binding.etExpiration.setSelection(binding.etExpiration.getText().toString().length());
                }
            }
        }
    }

    // popup date picker
    private void popupDatePicker(String type, String otherDate) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;

            String dateTime = tool.fmNewDate(selectedYear + " " + (selectedMonth + 1) + " " + selectedDay);
            if (type.equals("from")) {
                if (!otherDate.equals("")) {
                    if (tool.stringToDate(dateTime).after(tool.stringToDate(otherDate))) {
                        Toast.makeText(this, getString(R.string.from_date_is_invalid), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        long diff = tool.stringToDate(otherDate).getTime() - tool.stringToDate(dateTime).getTime();
                        binding.etAmount.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) * rentD));
                    }
                }
                binding.etFrom.setText(dateTime);
            } else {
                if (!otherDate.equals("")) {
                    if (tool.stringToDate(dateTime).before(tool.stringToDate(otherDate))) {
                        Toast.makeText(this, getString(R.string.till_date_is_invalid), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        long diff = tool.stringToDate(dateTime).getTime() - tool.stringToDate(otherDate).getTime();
                        binding.etAmount.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) * rentD));
                    }
                }
                binding.etTill.setText(dateTime);
            }
        }, selectedYear, selectedMonth, selectedDay);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
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
                Glide.with(this).load(bm).centerCrop().into(binding.ivImg);
                binding.btChooseFile.setVisibility(View.GONE);
            }
            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    bmImg = bm;
                    Glide.with(this).load(bm).centerCrop().into(binding.ivImg);
                    binding.rlImg.setVisibility(View.VISIBLE);
                    binding.btChooseFile.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // booking
    private void booking(){
        tool.showLoading();
        String from = binding.etFrom.getText().toString().trim();
        String till = binding.etTill.getText().toString().trim();
        String num = binding.etNum.getText().toString().trim();
        String amount = binding.etAmount.getText().toString().trim();
        String bgCheck = binding.etBg.getText().toString().trim();
        String license = binding.etLicense.getText().toString().trim();
        String cardNo = binding.etCardNo.getText().toString().trim();
        String cardName = binding.etCardName.getText().toString().trim();
        String exp = binding.etExpiration.getText().toString().trim();
        String cvv = binding.etCvv.getText().toString().trim();
        String zip = binding.etZip.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("pid", getIntent().getStringExtra("pid")).addFormDataPart("uid", pref.getUserId()).
                addFormDataPart("type", rentType).addFormDataPart("date", from).
                addFormDataPart("due", amount).addFormDataPart("background", bgCheck).
                addFormDataPart("card", cardNo).addFormDataPart("cname", cardName).
                addFormDataPart("expiration", exp).addFormDataPart("zip", zip).
                addFormDataPart("cvv", cvv).addFormDataPart("license", license).
                addFormDataPart("phone", phone);
        if (rentType.equals("day")){
            builder.addFormDataPart("date2", till);
        }else {
            builder.addFormDataPart("date2", from);
        }
        RequestBody rqFile = RequestBody.create(tool.getFileFromBm(bmImg), MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("img", tool.getFileFromBm(bmImg).getName(), rqFile);
        api.postData("booking.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            Toast.makeText(Booking.this, getString(R.string.you_have_successfully_submitted_your_request), Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(Booking.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Booking.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}