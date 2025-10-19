package com.example.rpg.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Task;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskDetailFragment extends Fragment {

    private static final String ARG_TASK_ID = "arg_task_id";

    private long taskId;
    private TaskDao taskDao;
    private Task task;

    private TextView textName, textDesc, textCategory, textStatus,
            textDifficulty, textImportance, textExecution, textRepeating;

    public static TaskDetailFragment newInstance(long taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskDao = AppDatabase.get(requireContext()).taskDao();
        if (getArguments() != null) {
            taskId = getArguments().getLong(ARG_TASK_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        textName = view.findViewById(R.id.text_task_name);
        textDesc = view.findViewById(R.id.text_task_description);
        textCategory = view.findViewById(R.id.text_task_category);
        textStatus = view.findViewById(R.id.text_task_status);
        textDifficulty = view.findViewById(R.id.text_task_difficulty);
        textImportance = view.findViewById(R.id.text_task_importance);
        textExecution = view.findViewById(R.id.text_task_execution);
        textRepeating = view.findViewById(R.id.text_task_repeating);

        Button btnDone = view.findViewById(R.id.btn_status_done);
        Button btnPaused = view.findViewById(R.id.btn_status_paused);
        Button btnCancelled = view.findViewById(R.id.btn_status_cancelled);
        Button btnEdit = view.findViewById(R.id.btn_edit_task);
        Button btnDelete = view.findViewById(R.id.btn_delete_task);

        loadTask();

        btnDone.setOnClickListener(v -> updateStatus("done"));
        btnPaused.setOnClickListener(v -> updateStatus("paused"));
        btnCancelled.setOnClickListener(v -> updateStatus("cancelled"));

        btnEdit.setOnClickListener(v -> {
            if (getParentFragment() instanceof TaskFragment) {
                ((TaskFragment) getParentFragment()).showTaskDialog(task);
            }
        });

        btnDelete.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                taskDao.delete(task);
                requireActivity().runOnUiThread(() -> {
                    if (getParentFragment() instanceof TaskFragment) {
                        ((TaskFragment) getParentFragment()).refreshTasks();
                        getParentFragment().getParentFragmentManager().popBackStack();
                    }
                });
            });
        });

        return view;
    }

    private void loadTask() {
        Executors.newSingleThreadExecutor().execute(() -> {
            task = taskDao.getById(taskId);
            if (task != null) {
                requireActivity().runOnUiThread(() -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    textName.setText(task.name);
                    textDesc.setText(task.description);
                    textCategory.setText("Category #" + task.categoryId);
                    textStatus.setText("Status: " + task.status);
                    textDifficulty.setText("Difficulty XP: " + task.difficultyXP);
                    textImportance.setText("Importance XP: " + task.importanceXP);
                    textExecution.setText("Execution: " + sdf.format(task.executionTime));
                    textRepeating.setText(task.isRepeating ? "Repeats every " + task.repeatInterval + " " + task.repeatUnit
                            + " from " + (task.repeatStart != null ? sdf.format(task.repeatStart) : "?")
                            + " to " + (task.repeatEnd != null ? sdf.format(task.repeatEnd) : "?") : "Not repeating");
                });
            }
        });
    }

    private void updateStatus(String newStatus) {
        if (task == null) return;
        task.status = newStatus;
        Executors.newSingleThreadExecutor().execute(() -> {
            taskDao.update(task);
            requireActivity().runOnUiThread(() -> {
                textStatus.setText("Status: " + task.status);
                if (getParentFragment() instanceof TaskFragment) {
                    ((TaskFragment) getParentFragment()).refreshTasks();
                }
            });
        });
    }
}
