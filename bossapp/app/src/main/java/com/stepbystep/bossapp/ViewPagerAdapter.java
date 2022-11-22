package com.stepbystep.bossapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.stepbystep.bossapp.home.MenuManageFragment;
import com.stepbystep.bossapp.home.TruckReviewFragment;

public class  ViewPagerAdapter extends FragmentStateAdapter {
    int num, count;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,int num, int count) {
        super(fragmentActivity);
        this.num = num;
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (num == 0) {
            switch (position) {
                case 0:
                    return new MenuManageFragment();

                default:
                    return new TruckReviewFragment();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return count;
    }


}