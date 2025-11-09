package com.example.rpg.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpg.R;
import com.example.rpg.model.Category;
import com.example.rpg.model.Task;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayVH> {

    public interface OnDayClick {
        void onDayClicked(java.time.LocalDate date, List<Task> tasksForDay);
    }

    private List<CalendarMonthFragment.CalendarDayModel> dayModels;
    private List<Category> categories;
    private OnDayClick listener;

    public CalendarDayAdapter(List<CalendarMonthFragment.CalendarDayModel> dayModels,
                              List<Category> categories,
                              OnDayClick listener) {
        this.dayModels = dayModels;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayVH holder, int position) {
        CalendarMonthFragment.CalendarDayModel model = dayModels.get(position);
        holder.textDayNumber.setText(String.valueOf(model.date.getDayOfMonth()));

        // Set up preview recycler using your TaskAdapter layout. We will show up to 3 items
        // reuse existing TaskAdapter class if it accepts List<Task> in constructor
        // Otherwise create a tiny adapter that uses task_item layout but binds minimal info

        List<Task> preview = model.tasks.size() > 3 ? model.tasks.subList(0, 3) : model.tasks;
        TaskPreviewAdapter previewAdapter = new TaskPreviewAdapter(preview);
        holder.recyclerPreview.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerPreview.setAdapter(previewAdapter);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDayClicked(model.date, model.tasks);
        });
    }

    @Override
    public int getItemCount() {
        return dayModels.size();
    }

    static class DayVH extends RecyclerView.ViewHolder {
        TextView textDayNumber;
        androidx.recyclerview.widget.RecyclerView recyclerPreview;

        DayVH(@NonNull View itemView) {
            super(itemView);
            textDayNumber = itemView.findViewById(R.id.text_day_number);
            recyclerPreview = itemView.findViewById(R.id.recycler_tasks_preview);
        }
    }
}
