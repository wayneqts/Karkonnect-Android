package com.appmonarchy.karkonnex.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.BaseActivity;
import com.appmonarchy.karkonnex.activities.UserChat;
import com.appmonarchy.karkonnex.databinding.ItemMessagesBinding;
import com.appmonarchy.karkonnex.helper.AppConstrains;
import com.appmonarchy.karkonnex.model.Messages;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.VH> {
    BaseActivity context;
    List<Messages> list;

    public MessagesAdapter(BaseActivity context, List<Messages> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessagesBinding binding = ItemMessagesBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Messages mess = list.get(position);
        holder.bind.tvName.setText(mess.getUserName());
        holder.bind.tvMess.setText(mess.getMsg());
        holder.bind.ivSeen.setVisibility(mess.getReadStt().equals("0") ? View.VISIBLE : View.GONE);
        holder.bind.tvDate.setText(context.tool.fmTimeAgo(mess.getTime()));
        Glide.with(context).load(mess.getPic()).error(R.drawable.no_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.bind.ivUser);
        holder.itemView.setOnClickListener(v -> ((Activity) context).startActivityForResult(new Intent(context, UserChat.class).putExtra("data", mess), AppConstrains.REFRESH_MESS_CODE));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemMessagesBinding bind;
        public VH(@NonNull ItemMessagesBinding itemMessagesBinding) {
            super(itemMessagesBinding.getRoot());
            this.bind = itemMessagesBinding;
        }
    }
}
