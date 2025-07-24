package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.adapter.GridAdapter;
import com.appmonarchy.karkonnex.databinding.FrmHomeBinding;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmHome extends Fragment {
    FrmHomeBinding binding;
    Home activity;
    List<CarInfo> list;
    GridAdapter adapter;
    public FrmHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmHomeBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new GridAdapter(activity, list, "view", binding.tvNoData);
        binding.rcv.setAdapter(adapter);
        getData();
    }

    // get data
    private void getData(){
        activity.tool.showLoading();
        activity.api.getArrById("showcase.php", activity.pref.getUserId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JSONArray jsonArray;
                list.clear();
                if (response.isSuccessful()){
                    try {
                        jsonArray = new JSONArray(String.valueOf(response.body()));
                        for (int i = 0;i < jsonArray.length();i++){
                            JSONObject jsObj = (JSONObject) jsonArray.get(i);
                            list.add(new CarInfo(jsObj.optString("id"), jsObj.optString("pid"), jsObj.optString("type"),  jsObj.optString("uid"),
                                    jsObj.optString("phone"), jsObj.optString("email"), jsObj.optString("address"), jsObj.optString("discription"),
                                    jsObj.optString("make"), jsObj.optString("modal"), jsObj.optString("year"), jsObj.optString("rent"),
                                    jsObj.optString("rentw"), jsObj.optString("rentm"), jsObj.optString("img1"),  jsObj.optString("img2"),
                                    jsObj.optString("img3"),  jsObj.optString("img4"), jsObj.optString("city"), jsObj.optString("zip"),
                                    jsObj.optString("state"), jsObj.optString("country"), jsObj.optString("created")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                activity.tool.hideLoading();
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                activity.tool.hideLoading();
            }
        });
    }
}