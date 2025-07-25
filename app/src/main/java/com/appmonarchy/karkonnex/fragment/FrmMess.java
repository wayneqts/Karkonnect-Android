package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.adapter.MessagesAdapter;
import com.appmonarchy.karkonnex.databinding.FrmMessBinding;
import com.appmonarchy.karkonnex.model.Messages;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMess extends Fragment {
    FrmMessBinding binding;
    Home activity;
    List<Messages> list;
    MessagesAdapter adapter;
    public FrmMess() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmMessBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        adapter = new MessagesAdapter(activity, list);
        binding.rcv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rcv.setAdapter(adapter);
        activity.tool.showLoading();
        getData();
    }

    // get data
    private void getData(){
        activity.api.getArrBysId("messages.php", activity.pref.getUserId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                list.clear();
                if (response.isSuccessful()){
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        if (jsArr.length() > 0){
                            for (int i = 0;i < jsArr.length();i++){
                                JSONObject obj = (JSONObject) jsArr.get(i);
                                list.add(new Messages(obj.optString("id1"), obj.optString("id2"), obj.optString("username"), obj.optString("msg"),
                                        obj.optString("pic"), obj.optString("readstatus"), obj.optString("time")));
                            }
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
                binding.tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}