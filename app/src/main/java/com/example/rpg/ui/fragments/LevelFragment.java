package com.example.rpg.ui.fragments;

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

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
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
            Log.i("ROWS AFFECTED", rowsAffected == 1 ? "1" : "Error passing task.");
        }).start();
    }
}