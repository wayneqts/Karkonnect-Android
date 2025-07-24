package com.appmonarchy.karkonnex.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.authentication.Login;
import com.appmonarchy.karkonnex.activities.authentication.SignUp;
import com.appmonarchy.karkonnex.databinding.ItemShowcaseBinding;
import com.appmonarchy.karkonnex.databinding.PopupRqLoginBinding;
import com.appmonarchy.karkonnex.helper.CustomTextMarkDown;
import com.appmonarchy.karkonnex.helper.Tool;
import com.appmonarchy.karkonnex.model.ShowCase;
import com.bumptech.glide.Glide;

import java.util.List;

public class ShowCaseAdapter extends RecyclerView.Adapter<ShowCaseAdapter.VH> {
    Context context;
    List<ShowCase> list;
    Tool tool;

    public ShowCaseAdapter(Context context, List<ShowCase> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShowcaseBinding binding = ItemShowcaseBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        tool = new Tool(context);
        ShowCase sc = list.get(position);
        holder.bind.tvName.setText(sc.getName());
        holder.bind.tvLocation.setText(sc.getAddress());
        holder.bind.tvPrice.setText(sc.getPrice());
        Glide.with(context).load(sc.getImg()).into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> popupRq());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemShowcaseBinding bind;
        public VH(@NonNull ItemShowcaseBinding itemShowcaseBinding) {
            super(itemShowcaseBinding.getRoot());
            this.bind = itemShowcaseBinding;
        }
    }

    // popup require login
    private void popupRq(){
        Dialog dialog = new Dialog(context);
        PopupRqLoginBinding rqLoginBinding = PopupRqLoginBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(rqLoginBinding.getRoot());
        tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        SpannedString loginText = (SpannedString) context.getText(R.string.please_login_if_already_member);
        Annotation[] annotationsLogin = loginText.getSpans(0, loginText.length(), Annotation.class);
        SpannableString loginCopy = new SpannableString(loginText);
        for (Annotation annotation : annotationsLogin) {
            if (annotation.getKey().equals("action")) {
                loginCopy.setSpan(
                        createClickSpan(annotation.getValue(), dialog),
                        loginText.getSpanStart(annotation),
                        loginText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        rqLoginBinding.tvToLogin.setText(loginCopy);
        rqLoginBinding.tvToLogin.setMovementMethod(LinkMovementMethod.getInstance());
        SpannedString signupText = (SpannedString) context.getText(R.string.join_matcheron_free_signup);
        Annotation[] annotationsSignup = signupText.getSpans(0, signupText.length(), Annotation.class);
        SpannableString signupCopy = new SpannableString(signupText);
        for (Annotation annotation : annotationsSignup) {
            if (annotation.getKey().equals("action")) {
                signupCopy.setSpan(
                        createClickSpan(annotation.getValue(), dialog),
                        signupText.getSpanStart(annotation),
                        signupText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        rqLoginBinding.tvToSignup.setText(signupCopy);
        rqLoginBinding.tvToSignup.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.show();
    }

    // create link span
    private CustomTextMarkDown createClickSpan(String action, Dialog dialog) {
        switch (action.toLowerCase()) {
            case "login":
                return new CustomTextMarkDown(() -> {
                    context.startActivity(new Intent(context, Login.class));
                    dialog.dismiss();
                }, "#1B1464", context);
            case "signup":
                return new CustomTextMarkDown(() -> {
                    context.startActivity(new Intent(context, SignUp.class));
                    dialog.dismiss();
                }, "#1B1464", context);
            default:
                throw new UnsupportedOperationException("action " + action + " not implemented");
        }
    }
}
