package com.example.rpg.ui.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.time.YearMonth;
import java.util.List;

public class MonthPagerAdapter extends FragmentStateAdapter {

    private List<YearMonth> months;

    public MonthPagerAdapter(@NonNull Fragment fragment, List<YearMonth> months) {
        super(fragment);
        this.months = months;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CalendarMonthFragment.newInstance(months.get(position));
    }

    @Override
    public int getItemCount() {
        return months.size();
    }
}
