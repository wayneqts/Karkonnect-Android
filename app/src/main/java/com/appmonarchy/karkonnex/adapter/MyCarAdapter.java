package com.appmonarchy.karkonnex.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.activities.EditCar;
import com.appmonarchy.karkonnex.activities.Home;
import com.appmonarchy.karkonnex.api.APIService;
import com.appmonarchy.karkonnex.api.APIUtils;
import com.appmonarchy.karkonnex.databinding.ItemMyCarBinding;
import com.appmonarchy.karkonnex.fragment.FrmMyCar;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.VH> {
    Home context;
    List<CarInfo> list;
    FrmMyCar frm;

    public MyCarAdapter(Home context, List<CarInfo> list, FrmMyCar frm) {
        this.context = context;
        this.list = list;
        this.frm = frm;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyCarBinding binding = ItemMyCarBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CarInfo carInfo = list.get(position);
        holder.bind.tvId.setText(carInfo.getpId());
        holder.bind.tvPrice.setText("Price: $"+carInfo.getPriceDay());
        holder.bind.tvAddress.setText(carInfo.getAddress());
        Glide.with(context).load(carInfo.getImg1()).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.bind.iv);
        holder.bind.btDelete.setOnClickListener(v -> deleteCar(position));
        holder.bind.btEdit.setOnClickListener(v -> context.startActivityForResult(new Intent(context, EditCar.class).putExtra("data", carInfo), AppConstrains.REFRESH_CAR_CODE));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemMyCarBinding bind;

        public VH(@NonNull ItemMyCarBinding itemMyCarBinding) {
            super(itemMyCarBinding.getRoot());
            this.bind = itemMyCarBinding;
        }
    }

    // delete car
    private void deleteCar(int pos){
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setMessage(context.getString(R.string.do_you_want_to_delete))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    context.tool.showLoading();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM).addFormDataPart("id", list.get(pos).getId());
                    APIService api = APIUtils.getAPIService();
                    api.postData("delete_car.php", builder.build()).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            list.remove(pos);
                            notifyDataSetChanged();
                            frm.binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                            context.tool.hideLoading();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
