package com.example.rpg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rpg.R;
import com.example.rpg.model.Task;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public interface OnAction {
        void onClick(Task t, int pos, View row);
    }

    private final OnAction onAction;

    public TaskAdapter(@NonNull Context context, @NonNull List<Task> data, @NonNull OnAction onAction) {
        super(context, 0, data);

        this.onAction = onAction;
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View convert, @NonNull ViewGroup parent) {
        View v = convert;
//        if (v == null) {
//            v = LayoutInflater.from(getContext()).inflate(R.layout.task_row, parent, false);
//        }
//
//        Task t = getItem(pos);
//        if (t == null) return v;
//
//        TextView title = v.findViewById(R.id.task_title_text);
//        TextView xp = v.findViewById(R.id.task_xp_text);
//        Button btn = v.findViewById(R.id.pass_task_button);
//
//        title.setText(String.format("Task %s", t.name));
//        xp.setText(String.format("Xp: %s", t.xp));
//
//        if (t.isPassed) {
//            btn.setEnabled(false);
//        }
//        else {
//            final View row = v;
//            btn.setEnabled(true);
//            btn.setOnClickListener(view -> onAction.onClick(t, pos, row));
//        }
//
        return v;
    }
}
