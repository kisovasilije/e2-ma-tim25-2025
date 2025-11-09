package com.example.rpg.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rpg.R;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {

    private ViewPager2 monthPager;

    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        monthPager = v.findViewById(R.id.monthPager);

        List<YearMonth> months = generateMonthsAroundToday(12); // 12 months total (6 back, 5 forward)
        MonthPagerAdapter adapter = new MonthPagerAdapter(this, months);
        monthPager.setAdapter(adapter);
        monthPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // set current month page
        int idx = months.indexOf(YearMonth.now());
        if (idx >= 0) monthPager.setCurrentItem(idx, false);

        return v;
    }

    // generate list of YearMonth centered on current month
    private List<YearMonth> generateMonthsAroundToday(int totalMonths) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth current = YearMonth.now();
        int half = totalMonths / 2;
        for (int i = -half; i < totalMonths - half; i++) {
            months.add(current.plusMonths(i));
        }
        return months;
    }
}
