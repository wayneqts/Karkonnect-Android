package com.appmonarchy.karkonnex.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.activities.CarDetail;
import com.appmonarchy.karkonnex.api.APIService;
import com.appmonarchy.karkonnex.api.APIUtils;
import com.appmonarchy.karkonnex.databinding.ItemGridBinding;
import com.appmonarchy.karkonnex.model.CarInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.VH> {
    BaseActivity context;
    List<CarInfo> list;
    String type;
    TextView tvNoData;

    public GridAdapter(BaseActivity context, List<CarInfo> list, String type, TextView tvNoData) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.tvNoData = tvNoData;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGridBinding binding = ItemGridBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CarInfo carInfo = list.get(position);

        holder.bind.tvPrice.setText("$"+carInfo.getPriceDay()+"/Day");
        holder.bind.tvCity.setText(carInfo.getAddress());
        if (carInfo.getType().equals("") || carInfo.getType().equals("1")){
            holder.bind.tvType.setText(context.getString(R.string.for_sale));
        }else {
            holder.bind.tvType.setText(context.getString(R.string.for_rent));
        }
        Glide.with(context).load(carInfo.getImg1()).error(R.drawable.img_no_car).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, CarDetail.class).putExtra("id", carInfo.getId())));
        if (type.equals("delete")){
            holder.bind.rlUsername.setVisibility(View.VISIBLE);
            holder.bind.tvUsername.setText("From: "+carInfo.getUsername());
            holder.bind.tvBt.setText(context.getString(R.string.delete));
            holder.bind.rlView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.app_name))
                    .setMessage(context.getString(R.string.do_you_want_to_delete))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        deleteItem(position);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .show());
        }else {
            holder.bind.tvBt.setText(context.getString(R.string.view));
            holder.bind.rlUsername.setVisibility(View.GONE);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(context.tool.stringToDate2(carInfo.getCreated()));
        c.add(Calendar.DAY_OF_MONTH, 1);
        if (new Date().after(c.getTime())){
            holder.bind.tvId.setText(carInfo.getpId()+" \uD83D\uDE99");
        }else {
            holder.bind.tvId.setText(carInfo.getpId());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemGridBinding bind;
        public VH(@NonNull ItemGridBinding itemGridBinding) {
            super(itemGridBinding.getRoot());
            this.bind = itemGridBinding;
        }
    }

    // delete item
    private void deleteItem(int pos){
        context.tool.showLoading();
        APIService api = APIUtils.getAPIService();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("id", list.get(pos).getId());
        api.postData("delete_inquiry.php", builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("status")){
                            list.remove(pos);
                            notifyDataSetChanged();
                            tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                context.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                context.tool.hideLoading();
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
