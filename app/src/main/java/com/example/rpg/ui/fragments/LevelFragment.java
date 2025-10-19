package com.example.rpg.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.databinding.FragmentLevelBinding;
import com.example.rpg.model.Task;
import com.example.rpg.model.User;
import com.example.rpg.model.UserProgress;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.ui.activities.MainActivity;
import com.example.rpg.ui.adapters.TaskAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LevelFragment extends Fragment {
    private FragmentLevelBinding binding;

    private AppDatabase db;

    private TaskAdapter adapter;

    private User user;

    private UserProgress progress;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        db = AppDatabase.get(context.getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLevelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        var username = AuthPrefs.getIsAuthenticated(requireContext());
        Log.i("USERNAME", username != null ? username : "NO USERNAME");

        new Thread(() -> {
            user = db.userDao().getByUsername(username);
            progress = db.userProgressDao().getById(user.id);

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    setStats();
                    setTasks();
                    return;
                }

                if(getContext() == null) return;

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setAuth(true);
                }

                var nav = NavHostFragment.findNavController(this);
                var opts = new NavOptions.Builder()
                        .setPopUpTo(R.id.base_navigation, true)
                        .build();

                nav.navigate(R.id.nav_login, null, opts);
            });
        }).start();
    }

    @SuppressLint("DefaultLocale")
    private void setStats() {
        binding.titleText.setText(progress.title);
        binding.ppText.setText(String.format("%d", progress.pp));
        binding.xpText.setText(String.format("%d", progress.xp));
        binding.xpLeftText.setText(String.format("%d", progress.xpCap - progress.xp));
        binding.levelText.setText(String.format("%d", progress.level));
    }

    @SuppressLint("DefaultLocale")
    private void setStats(UserProgress progress) {
        binding.titleText.setText(progress.title);
        binding.ppText.setText(String.format("%d", progress.pp));
        binding.xpText.setText(String.format("%d", progress.xp));
        binding.xpLeftText.setText(String.format("%d", progress.xpCap - progress.xp));
        binding.levelText.setText(String.format("%d", progress.level));
    }

    private void setTasks() {
        new Thread(() -> {
            List<Task> tasks = db.taskDao().getByUserId(user.id);

            requireActivity().runOnUiThread(() -> {
                ListView lw = binding.tasks;

                adapter = new TaskAdapter(requireContext(), tasks, this::onAction);
                lw.setAdapter(adapter);
            });
        }).start();
    }

    private void onAction(Task t, int pos, View row) {
        new Thread(() -> {
            t.isPassed = true;
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            int rowsAffected = db.taskDao().passTask(t.id);
            Log.i("RA UPDATE TASK", rowsAffected == 1 ? "1" : "Error passing task.");

            var progress = db.userProgressDao().getById(user.id);
            progress.update(t);
            rowsAffected = db.userProgressDao().update(progress);
            Log.i("RA UPDATE PROGRESS", rowsAffected == 1 ? "1" : "Error updating progress.");
            if (rowsAffected > 0) {
                requireActivity().runOnUiThread(() -> this.setStats(progress));
            }
        }).start();
    }
}