package com.example.rpg.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rpg.R;
import com.example.rpg.model.Task;

import java.util.List;

public class TaskPreviewAdapter extends RecyclerView.Adapter<TaskPreviewAdapter.PVH> {

    private List<Task> tasks;

    public TaskPreviewAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public PVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse an existing small layout if you have one. Fallback: small inline layout
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PVH holder, int position) {
        Task t = tasks.get(position);
        holder.text.setText(t.name != null ? t.name : "Unnamed");
        holder.itemView.setOnClickListener(v -> {
            // Clicking a preview should open TaskDetailFragment for that task id
            // We'll get activity FragmentManager by walking context -> assume it's a FragmentActivity
            androidx.fragment.app.FragmentActivity act = (androidx.fragment.app.FragmentActivity) v.getContext();
            act.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, TaskDetailFragment.newInstance(t.id))
                    .addToBackStack(null)
                    .commit();

        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    static class PVH extends RecyclerView.ViewHolder {
        TextView text;
        PVH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
            text.setMaxLines(1);
        }
    }
}
