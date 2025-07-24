package com.appmonarchy.karkonnex.adapter.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.appmonarchy.karkonnex.fragment.FrmReceived;
import com.appmonarchy.karkonnex.fragment.FrmSent;

public class InquiryAdapter extends FragmentStateAdapter {
    public InquiryAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new FrmReceived();
        }else {
            return new FrmSent();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
