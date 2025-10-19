package com.example.rpg.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.CategoryDao;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Category;
import com.example.rpg.model.Player;
import com.example.rpg.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskFragment extends Fragment {

    private AppDatabase db;
    private TaskDao taskDao;
    private CategoryDao categoryDao;

    private TaskListFragment nonRepeatingFragment;
    private TaskListFragment repeatingFragment;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private Player currentPlayer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = AppDatabase.get(context.getApplicationContext());
        taskDao = db.taskDao();
        categoryDao = db.categoryDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_task);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        Executors.newSingleThreadExecutor().execute(() -> {
            currentPlayer = db.playerDao().getCurrentPlayer();
        });

        fabAdd.setOnClickListener(v -> showTaskDialog(null));

        setupTabs();
        return view;
    }

    private void setupTabs() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> allTasks = taskDao.getCurrentAndFutureTasks(new Date());
            List<Category> allCategories = categoryDao.getAll();

            requireActivity().runOnUiThread(() -> {
                nonRepeatingFragment = TaskListFragment.newInstance(allTasks, allCategories, false);
                repeatingFragment = TaskListFragment.newInstance(allTasks, allCategories, true);

                viewPager.setAdapter(new androidx.viewpager2.adapter.FragmentStateAdapter(this) {
                    @NonNull
                    @Override
                    public Fragment createFragment(int position) {
                        return position == 0 ? nonRepeatingFragment : repeatingFragment;
                    }

                    @Override
                    public int getItemCount() {
                        return 2;
                    }
                });

                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> tab.setText(position == 0 ? "Non-Repeating" : "Repeating"))
                        .attach();
            });
        });
    }

    public void refreshTasks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> allTasks = taskDao.getCurrentAndFutureTasks(new Date());
            requireActivity().runOnUiThread(() -> {
                if (nonRepeatingFragment != null) nonRepeatingFragment.updateTasks(allTasks);
                if (repeatingFragment != null) repeatingFragment.updateTasks(allTasks);
            });
        });
    }

    public void showTaskDialog(@Nullable Task task) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = categoryDao.getAll();

            requireActivity().runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_task, null);
                builder.setView(dialogView);

                EditText inputName = dialogView.findViewById(R.id.input_task_name);
                EditText inputDescription = dialogView.findViewById(R.id.input_task_description);
                Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_task_category);
                Spinner spinnerDifficulty = dialogView.findViewById(R.id.spinner_task_difficulty);
                Spinner spinnerImportance = dialogView.findViewById(R.id.spinner_task_importance);
                Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_task_status);
                CheckBox checkRepeating = dialogView.findViewById(R.id.checkbox_repeating);
                EditText inputInterval = dialogView.findViewById(R.id.input_repeat_interval);
                Spinner spinnerUnit = dialogView.findViewById(R.id.spinner_repeat_unit);
                DatePicker dateExecution = dialogView.findViewById(R.id.date_execution);
                DatePicker dateRepeatStart = dialogView.findViewById(R.id.date_repeat_start);
                DatePicker dateRepeatEnd = dialogView.findViewById(R.id.date_repeat_end);

                List<String> catNames = new ArrayList<>();
                for (Category c : categories) catNames.add(c.name);
                spinnerCategory.setAdapter(new android.widget.ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, catNames));

                String[] difficultyOptions = {"Very Easy - 1 XP", "Easy - 3 XP", "Hard - 7 XP", "Extremely Hard - 20 XP"};
                spinnerDifficulty.setAdapter(new android.widget.ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, difficultyOptions));

                String[] importanceOptions = {"Normal - 1 XP", "Important - 3 XP", "Extremely Important - 10 XP", "Special - 100 XP"};
                spinnerImportance.setAdapter(new android.widget.ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, importanceOptions));

                String[] units = {"Day", "Week"};
                spinnerUnit.setAdapter(new android.widget.ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, units));

                String[] statusOptions = {"paused", "active", "done"};
                spinnerStatus.setAdapter(new android.widget.ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, statusOptions));

                if (task != null) {
                    inputName.setText(task.name);
                    inputDescription.setText(task.description);
                    checkRepeating.setChecked(task.isRepeating);
                    inputInterval.setText(String.valueOf(task.repeatInterval));
                    spinnerUnit.setSelection(task.repeatUnit != null && task.repeatUnit.equals("Week") ? 1 : 0);
                }

                builder.setTitle(task == null ? "Add Task" : "Edit Task");
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    String description = inputDescription.getText().toString().trim();
                    int categoryPos = spinnerCategory.getSelectedItemPosition();
                    Long categoryId = categoryPos >= 0 && categoryPos < categories.size()
                            ? categories.get(categoryPos).id : null;
                    boolean repeating = checkRepeating.isChecked();
                    int interval = repeating && !inputInterval.getText().toString().isEmpty()
                            ? Integer.parseInt(inputInterval.getText().toString()) : 0;
                    String unit = repeating ? spinnerUnit.getSelectedItem().toString() : null;

                    Calendar execCal = Calendar.getInstance();
                    execCal.set(dateExecution.getYear(), dateExecution.getMonth(), dateExecution.getDayOfMonth());
                    Date executionTime = execCal.getTime();

                    Calendar startCal = Calendar.getInstance();
                    startCal.set(dateRepeatStart.getYear(), dateRepeatStart.getMonth(), dateRepeatStart.getDayOfMonth());
                    Date repeatStart = repeating ? startCal.getTime() : null;

                    Calendar endCal = Calendar.getInstance();
                    endCal.set(dateRepeatEnd.getYear(), dateRepeatEnd.getMonth(), dateRepeatEnd.getDayOfMonth());
                    Date repeatEnd = repeating ? endCal.getTime() : null;

                    int difficultyXP = getDifficultyXPFromIndex(spinnerDifficulty.getSelectedItemPosition());
                    int importanceXP = getImportanceXPFromIndex(spinnerImportance.getSelectedItemPosition());
                    Long playerOwnerId = (currentPlayer != null) ? currentPlayer.id : null;
                    Long stageId = (currentPlayer != null) ? (long) currentPlayer.level : null;

                    Task newTask = new Task(
                            name,
                            description,
                            categoryId,
                            playerOwnerId,
                            stageId,
                            repeating,
                            interval,
                            unit,
                            repeatStart,
                            repeatEnd,
                            difficultyXP,
                            importanceXP,
                            executionTime
                    );

                    newTask.status = (task == null) ? "active" : spinnerStatus.getSelectedItem().toString();

                    Executors.newSingleThreadExecutor().execute(() -> {
                        if (task == null) taskDao.insert(newTask);
                        else {
                            newTask.id = task.id;
                            taskDao.update(newTask);
                        }
                        requireActivity().runOnUiThread(this::refreshTasks);
                    });
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            });
        });
    }

    private int getDifficultyXPFromIndex(int index) {
        switch (index) {
            case 0: return 1;
            case 1: return 3;
            case 2: return 7;
            case 3: return 20;
            default: return 1;
        }
    }

    private int getImportanceXPFromIndex(int index) {
        switch (index) {
            case 0: return 1;
            case 1: return 3;
            case 2: return 10;
            case 3: return 100;
            default: return 1;
        }
    }
}
