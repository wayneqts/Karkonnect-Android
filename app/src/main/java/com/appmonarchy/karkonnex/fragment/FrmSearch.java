package com.appmonarchy.karkonnex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.adapter.CustomSpinnerAdapter;
import com.appmonarchy.karkonnex.adapter.GridAdapter;
import com.appmonarchy.karkonnex.databinding.FrmSearchBinding;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.appmonarchy.karkonnex.model.Goal;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmSearch extends Fragment {
    FrmSearchBinding binding;
    Home activity;
    List<CarInfo> list;
    GridAdapter adapter;
    String stateId = "", countryId = "231", type = "2";
    List<Goal> stateList, countryList;
    public FrmSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmSearchBinding.inflate(getLayoutInflater());
        init();

        binding.btSearch.setOnClickListener(v -> getData());
        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(activity.getColor(R.color.grey1));
                }else {
                    stateId = stateList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    ((TextView) view).setTextColor(activity.getColor(R.color.grey1));
//                } else {
//                    countryId = countryList.get(position).getId();
//                    getState(countryId);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        binding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_rent) {
                type = "0";
            } else if (checkedId == R.id.rb_sell){
                type = "1";
            }else {
                type = "2";
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new GridAdapter(activity, list, "view", binding.tvNoData);
        binding.rcv.setAdapter(adapter);
        getData();
//        getCountry();
        getState();
    }

    // get data
    private void getData(){
        activity.tool.showLoading();
        activity.api.search("search.php", stateId, binding.etCity.getText().toString().trim(), binding.etZipCode.getText().toString().trim(), type, countryId).enqueue(new Callback<JsonArray>() {
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
                list.clear();
                adapter.notifyDataSetChanged();
                activity.tool.hideLoading();
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    // get country
    private void getCountry() {
        activity.api.getDataArr("countries.php").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                if (countryArr != null) {
//                    CustomSpinnerAdapter countryAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, countryArr);
//                    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    binding.spinnerCountry.setAdapter(countryAdapter);
//                }
//                binding.pbCt.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get state
    private void getState() {
        binding.spinnerState.setVisibility(View.GONE);
        binding.ivDown.setVisibility(View.GONE);
        binding.pbState.setVisibility(View.VISIBLE);
        activity.api.getArrById("state.php", "231").enqueue(new Callback<JsonArray>() {
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
                    CustomSpinnerAdapter stateAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, stateArr);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerState.setAdapter(stateAdapter);
                }
                binding.spinnerState.setVisibility(View.VISIBLE);
                binding.ivDown.setVisibility(View.VISIBLE);
                binding.pbState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}