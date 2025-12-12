package com.example.rpg.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rpg.database.AppDatabase;
import com.example.rpg.databinding.FragmentStatsBinding;
import com.example.rpg.model.statistics.StatisticsResult;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.services.StatisticsService;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {
    private static final String TAG = StatsFragment.class.getSimpleName();

    private FragmentStatsBinding binding;

    private AppDatabase db;

    private StatisticsResult result;

    private StatisticsService statisticsService;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        db = AppDatabase.get(context.getApplicationContext());
        statisticsService = new StatisticsService(db);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        var username = AuthPrefs.getIsAuthenticated(requireContext());
        if (username == null || username.isBlank()) {
            Log.w(TAG, "init: user not logged in.");
            return;
        }

        executor.execute(() -> {
            var user = db.userDao().getByUsername(username);
            if (user == null) {
                Log.e(TAG, "init: user doesn't exist.");
                return;
            }

            Log.i(TAG, "init: user logged in.");
            statisticsService.setUserId(user.id);

            result = statisticsService.calculate();

            requireActivity().runOnUiThread(this::populateUi);
        });
    }

    private String generateMsg(String prefix, String content) {
        return String.format("%s: %s", prefix, content);
    }

    private void populateUi() {
        binding.activeDaysText.setText(generateMsg("Active days", String.valueOf(result.activeDays)));
        setTaskStatusChart();
    }

    private void setTaskStatusChart() {
        var stats = result.taskStatusStats;

        List<PieEntry> entries = new ArrayList<>();
        if (stats.tasksCreated > 0) {
            entries.add(new PieEntry(stats.tasksCreated, "Created"));
        }

        if (stats.tasksDone > 0) {
            entries.add(new PieEntry(stats.tasksDone, "Done"));
        }

        if (stats.tasksUnfinished > 0) {
            entries.add(new PieEntry(stats.tasksUnfinished, "Unfinished"));
        }

        if (stats.tasksCanceled > 0) {
            entries.add(new PieEntry(stats.tasksCanceled, "Canceled"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        binding.taskStatusChart.setData(data);

        Description d = new Description();
        d.setText("Tasks (created/done/unfinished/canceled)");
        binding.taskStatusChart.setDescription(d);

        binding.taskStatusChart.invalidate();
    }
}