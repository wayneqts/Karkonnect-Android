package com.appmonarchy.karkonnex.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.appmonarchy.karkonnex.R;
import com.appmonarchy.karkonnex.activities.Privacy;
import com.appmonarchy.karkonnex.activities.Term;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {
    Context context;
    Dialog dialog;

    public Tool(Context context) {
        this.context = context;
    }

    // show loading
    public void showLoading() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pb_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // hide loading
    public void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    // setup dialog
    public void setupDialog(Dialog dialog, int gravity, int height) {
        dialog.getWindow().setGravity(gravity);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // check email valid
    public boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // textview mark down
    public void tvMarkDown(int res, TextView tv){
        SpannedString termsText = (SpannedString) context.getText(res);
        Annotation[] annotations = termsText.getSpans(0, termsText.length(), Annotation.class);
        SpannableString termsCopy = new SpannableString(termsText);
        for (Annotation annotation : annotations) {
            if (annotation.getKey().equals("action")) {
                termsCopy.setSpan(
                        createClickSpan(annotation.getValue()),
                        termsText.getSpanStart(annotation),
                        termsText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        tv.setText(termsCopy);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // convert bitmap to file
    public File getFileFromBm(Bitmap bm){
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
        Uri uri = Uri.parse(path);
        String realUri = RealPathUtil.getRealPath(context, uri);
        return new File(realUri);
    }

    // create link span
    private CustomClickSpan createClickSpan(String action) {
        switch (action.toLowerCase()) {
            case "privacy":
                return new CustomClickSpan(() -> context.startActivity(new Intent(context, Privacy.class)), "#810B10");
            case "term":
                return new CustomClickSpan(() -> context.startActivity(new Intent(context, Term.class)), "#810B10");
            default:
                throw new UnsupportedOperationException("action " + action + " not implemented");
        }
    }

    // format time ago
    public String fmTimeAgo(String time){
        return new FmTimeAgo().covertTimeToText(time);
    }

    // open web browser
    public void openWeb(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    // recyclerview no blink
    public void rcvNoAnimator(RecyclerView rcv) {
        RecyclerView.ItemAnimator animator = rcv.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        rcv.getItemAnimator().setChangeDuration(0);
    }

    // disable button
    public void disableBt(View v) {
        v.setEnabled(false);
        v.setAlpha(.5f);
    }

    // enable button
    public void enableBt(View v) {
        v.setEnabled(true);
        v.setAlpha(1);
    }

    // hide keyboard
    public void hideKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // format date
    public String fmNewDate(String date) {
        String dayFm = "";
        SimpleDateFormat fm = new SimpleDateFormat("yyyy M d", Locale.ENGLISH);
        SimpleDateFormat fmNew = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date newDate = fm.parse(date);
            dayFm = fmNew.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayFm;
    }

    // string to date
    public Date stringToDate(String date) {
        Date newDate = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            newDate = fm.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    // string to date
    public Date stringToDate2(String date) {
        Date newDate = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            newDate = fm.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    // imageview to bitmap
    public Bitmap imgToBm(ImageView iv){
        BitmapDrawable bd = (BitmapDrawable) iv.getDrawable();
        if (bd != null){
            return bd.getBitmap();
        }else {
            return null;
        }
    }
}
