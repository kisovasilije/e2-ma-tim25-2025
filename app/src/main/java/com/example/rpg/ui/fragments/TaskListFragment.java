package com.example.rpg.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.managers.ProgressManager;
import com.example.rpg.model.Category;
import com.example.rpg.model.Task;
import com.example.rpg.model.UserProgress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskListFragment extends Fragment {

    private boolean showRepeating;
    private List<Task> allTasks = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private TaskAdapter adapter;
    private TextView emptyView;
    public static TaskListFragment newInstance(List<Task> tasks, List<Category> categories, boolean showRepeating) {
        TaskListFragment fragment = new TaskListFragment();
        fragment.showRepeating = showRepeating;
        fragment.allTasks = tasks != null ? tasks : new ArrayList<>();
        fragment.allCategories = categories != null ? categories : new ArrayList<>();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_tasks);
        emptyView = view.findViewById(R.id.text_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(this);
        recyclerView.setAdapter(adapter);

        updateTasks(allTasks);

        return view;
    }
    public void updateTasks(List<Task> newTasks) {
        this.allTasks = newTasks != null ? newTasks : new ArrayList<>();

        List<Task> filtered = new ArrayList<>();
        for (Task t : allTasks) {
            if (t.isRepeating == showRepeating) filtered.add(t);
        }

        if (adapter != null) adapter.setTasks(filtered);

        if (emptyView != null) {
            emptyView.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateTasks(allTasks);
    }
    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private List<Task> tasks = new ArrayList<>();
        private final Fragment parentFragment;
        TaskAdapter(Fragment parent) {
            this.parentFragment = parent;
        }
        public void setTasks(List<Task> newTasks) {
            this.tasks = (newTasks == null)
                    ? new ArrayList<>()
                    : newTasks.stream()
                    .sorted((a, b) -> Long.compare(b.id, a.id))
                    .collect(Collectors.toList());

            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);

            holder.textName.setText(task.name != null ? task.name : "Unnamed Task");
            holder.textCategory.setText(
                    task.categoryId != null ? "Category #" + task.categoryId : "No Category");
            holder.textStatus.setText("Status: " + (task.status != null ? task.status : "Unknown"));

            boolean clickable = "active".equals(task.status) || "paused".equals(task.status);

            holder.itemView.setEnabled(clickable);
            holder.itemView.setAlpha(clickable ? 1f : 0.4f);

            holder.buttonDone.setOnClickListener(v -> {
                if (!clickable) return;

                holder.textStatus.setText("Status: done");
                holder.itemView.setAlpha(0.4f);
                holder.itemView.setEnabled(false);

                Context ctx = holder.itemView.getContext();
                AppDatabase db = AppDatabase.get(ctx);

                Executors.newSingleThreadExecutor().execute(() -> {

                    task.status = "done";
                    task.completionTime = new Date();

                    ProgressManager pm = new ProgressManager(db.taskDao());
                    int awardedXp = pm.calculateAwardedXp(task, task.userId);

                    task.totalXP = awardedXp;
                    db.taskDao().update(task);

                    UserProgress progress = db.userProgressDao().getById(task.userId);
                    if (progress != null) {
                        progress.xp += awardedXp;
                        if (awardedXp > 0) progress.update(task);
                        db.userProgressDao().update(progress);
                    }
                });
            });

            if (clickable) {
                holder.itemView.setOnClickListener(v -> {
                    if (parentFragment.getParentFragmentManager() != null) {
                        parentFragment.getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_task_detail, TaskDetailFragment.newInstance(task.id))
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else {
                holder.itemView.setOnClickListener(null);
            }
        }
        @Override
        public int getItemCount() {
            return tasks.size();
        }
        static class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textCategory, textStatus;
            View buttonDone;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.text_task_name);
                textCategory = itemView.findViewById(R.id.text_task_category);
                textStatus = itemView.findViewById(R.id.text_task_status);
                buttonDone = itemView.findViewById(R.id.button_done);
            }
        }
    }
}
