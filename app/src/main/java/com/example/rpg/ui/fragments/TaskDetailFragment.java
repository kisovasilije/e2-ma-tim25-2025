package com.example.rpg.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Task;
import com.example.rpg.model.User;
import com.example.rpg.model.UserProgress;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.ui.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskDetailFragment extends Fragment {

    private static final String ARG_TASK_ID = "arg_task_id";

    private long taskId;
    private TaskDao taskDao;
    private Task task;

    private TextView textName, textDesc, textCategory, textStatus,
            textDifficulty, textImportance, textExecution, textRepeating;

    private User user;
    private UserProgress progress;
    private AppDatabase db;

    public static TaskDetailFragment newInstance(long taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = AppDatabase.get(context.getApplicationContext());
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

        btnDone.setOnClickListener(v -> updateStatus(Task.DONE));
        btnPaused.setOnClickListener(v -> togglePauseStatus(btnPaused));
        btnCancelled.setOnClickListener(v -> updateStatus(Task.CANCELED));
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnDelete.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                if (task != null && getParentFragment() instanceof TaskFragment) {
                    ((TaskFragment) getParentFragment()).cancelCountdown(task.id);
                }

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
            if (user != null)
                progress = db.userProgressDao().getById(user.id);

            requireActivity().runOnUiThread(() -> {
                if (user != null) return;

                if (getContext() == null) return;

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
    private void togglePauseStatus(Button btnPaused) {
        if (task == null) return;

        String oldStatus = task.status;
        String newStatus;

        if (Task.PAUSED.equals(task.status)) {
            btnPaused.setText("Pause");
            newStatus = Task.ACTIVE;
        } else {
            btnPaused.setText("Reactivate");
            newStatus = Task.PAUSED;
        }

        task.status = newStatus;

        Executors.newSingleThreadExecutor().execute(() -> {
            taskDao.update(task);

            requireActivity().runOnUiThread(() -> {
                textStatus.setText("Status: " + task.status);

                if (getParentFragment() instanceof TaskFragment) {
                    TaskFragment parent = (TaskFragment) getParentFragment();

                    if (Task.ACTIVE.equals(newStatus) && Task.PAUSED.equals(oldStatus)) {
                        parent.startUnfinishedCountdown(task.id);
                    } else {
                        parent.cancelCountdown(task.id);
                    }

                    parent.refreshTasks();
                }

                getParentFragmentManager().popBackStack();
            });
        });
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
                    textRepeating.setText(task.isRepeating
                            ? "Repeats every " + task.repeatInterval + " " + task.repeatUnit
                            + " from " + (task.repeatStart != null ? sdf.format(task.repeatStart) : "?")
                            + " to " + (task.repeatEnd != null ? sdf.format(task.repeatEnd) : "?")
                            : "Not repeating");

                    Button btnPaused = requireView().findViewById(R.id.btn_status_paused);
                    if (Task.PAUSED.equals(task.status)) {
                        btnPaused.setText("Reactivate");
                    } else {
                        btnPaused.setText("Pause");
                    }
                });
            }
        });
    }
    private void showEditDialog() {
        if (task == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_task, null);

        EditText nameInput = dialogView.findViewById(R.id.edit_task_name);
        EditText descInput = dialogView.findViewById(R.id.edit_task_description);
        EditText diffInput = dialogView.findViewById(R.id.edit_task_difficulty);
        EditText impInput = dialogView.findViewById(R.id.edit_task_importance);
        DatePicker execPicker = dialogView.findViewById(R.id.edit_task_execution);

        nameInput.setText(task.name);
        descInput.setText(task.description);
        diffInput.setText(String.valueOf(task.difficultyXP));
        impInput.setText(String.valueOf(task.importanceXP));

        if (task.executionTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(task.executionTime);
            execPicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    if (getParentFragment() instanceof TaskFragment) {
                        ((TaskFragment) getParentFragment()).cancelCountdown(task.id);
                    }

                    String newName = nameInput.getText().toString().trim();
                    String newDesc = descInput.getText().toString().trim();
                    String diffStr = diffInput.getText().toString().trim();
                    String impStr = impInput.getText().toString().trim();

                    int newDiff = diffStr.isEmpty() ? task.difficultyXP : Integer.parseInt(diffStr);
                    int newImp = impStr.isEmpty() ? task.importanceXP : Integer.parseInt(impStr);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(execPicker.getYear(), execPicker.getMonth(), execPicker.getDayOfMonth());
                    Date newExecTime = calendar.getTime();

                    task.name = newName;
                    task.description = newDesc;
                    task.difficultyXP = newDiff;
                    task.importanceXP = newImp;
                    task.executionTime = newExecTime;

                    Executors.newSingleThreadExecutor().execute(() -> {
                        taskDao.update(task);

                        requireActivity().runOnUiThread(() -> {
                            if (getParentFragment() instanceof TaskFragment) {
                                ((TaskFragment) getParentFragment()).refreshTasks();
                            }
                            getParentFragmentManager().popBackStack();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void updateStatus(String newStatus) {
        if (task == null) return;

        task.status = newStatus;

        Executors.newSingleThreadExecutor().execute(() -> {
            taskDao.update(task);

            var progress = db.userProgressDao().getById(user.id);
            if (progress == null) {
                return;
            }

            progress.update(task);
            int rowsAffected = db.userProgressDao().update(progress);
            if (rowsAffected < 1) {
                return;
            }

            Log.d("[RPG]", "Task passed. Progress updated.");
            if (isAdded() && getActivity() != null) {
                requireActivity().runOnUiThread(() -> {

                    if (getParentFragment() instanceof TaskFragment) {
                        TaskFragment parent = (TaskFragment) getParentFragment();
                        parent.cancelCountdown(task.id);
                        parent.refreshTasks();
                    }

                    getParentFragment().getChildFragmentManager().popBackStack();
                });
            }
        });
    }
}
