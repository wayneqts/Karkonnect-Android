package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.adapter.GridAdapter;
import com.appmonarchy.karkonnex.databinding.FrmReceivedBinding;
import com.appmonarchy.karkonnex.databinding.FrmSentBinding;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmSent extends Fragment {
    FrmSentBinding binding;
    Home activity;
    List<CarInfo> list;
    GridAdapter adapter;
    public FrmSent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmSentBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new GridAdapter(activity, list, "delete", binding.tvNoData);
        binding.rcv.setAdapter(adapter);
        getData();
    }

    // get data
    private void getData(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("uid", activity.pref.getUserId());
        activity.api.postData("inquiry_listing2.php", builder.build()).enqueue(new Callback<JsonObject>() {
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
                                if (json instanceof JSONObject){
                                    JSONObject jsObj = (JSONObject) jsData.get(i);
                                    JSONObject jsCar = jsObj.optJSONObject("car");
                                    CarInfo carInfo = new CarInfo(jsCar.optString("id"), jsCar.optString("pid"), jsCar.optString("type"),  jsCar.optString("uid"),
                                            jsCar.optString("phone"), jsCar.optString("email"), jsCar.optString("address"), jsCar.optString("discription"),
                                            jsCar.optString("make"), jsCar.optString("modal"), jsCar.optString("year"), jsCar.optString("rent"),
                                            jsCar.optString("rentw"), jsCar.optString("rentm"), jsCar.optString("img1"),  jsCar.optString("img2"),
                                            jsCar.optString("img3"),  jsCar.optString("img4"), jsCar.optString("city"), jsCar.optString("zip"),
                                            jsCar.optString("state"), jsCar.optString("country"), jsCar.optString("created"));
                                    carInfo.setUsername(jsCar.optString("name"));
                                    list.add(carInfo);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}