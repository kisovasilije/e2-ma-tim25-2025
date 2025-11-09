package com.example.rpg.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.CategoryDao;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Category;
import com.example.rpg.model.Task;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarMonthFragment extends Fragment {

    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_MONTH = "arg_month";

    private YearMonth month;
    private RecyclerView recyclerDays;

    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private AppDatabase db;

    public static CalendarMonthFragment newInstance(YearMonth month) {
        CalendarMonthFragment f = new CalendarMonthFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_YEAR, month.getYear());
        b.putInt(ARG_MONTH, month.getMonthValue());
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.get(requireContext().getApplicationContext());
        taskDao = db.taskDao();
        categoryDao = db.categoryDao();

        if (getArguments() != null) {
            int y = getArguments().getInt(ARG_YEAR);
            int m = getArguments().getInt(ARG_MONTH);
            month = YearMonth.of(y, m);
        } else {
            month = YearMonth.now();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        recyclerDays = v.findViewById(R.id.recyclerDays);

        // 7 columns for week days
        GridLayoutManager gm = new GridLayoutManager(requireContext(), 7);
        recyclerDays.setLayoutManager(gm);

        loadDataAndBuildDays();

        return v;
    }

    private void loadDataAndBuildDays() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // fetch all tasks and categories (adjust if your DAO has different function names)
            List<Task> allTasks;
            try {
                allTasks = taskDao.getAll(); // <--- replace if your DAO method is different
            } catch (Exception e) {
                // fallback: try current+future (if getAll doesn't exist)
                allTasks = taskDao.getCurrentAndFutureTasks(new Date(0));
            }
            List<Category> categories = categoryDao.getAll();

            // prepare Day models (includes padding start-of-week days)
            List<LocalDate> daysToShow = buildMonthGridDays(month);

            // map each LocalDate -> list of tasks for that day
            List<CalendarDayModel> dayModels = new ArrayList<>();
            for (LocalDate d : daysToShow) {
                List<Task> tasksForDay = new ArrayList<>();
                // compare using Date timestamps:
                for (Task t : allTasks) {
                    Date start = t.repeatStart != null ? t.repeatStart : t.executionTime;
                    Date end = t.repeatEnd != null ? t.repeatEnd : t.executionTime;
                    // if either start or end null, skip conservative
                    if (start == null || end == null) {
                        // fallback: if executionTime exactly equals day
                        if (t.executionTime != null && sameLocalDate(t.executionTime, d)) {
                            tasksForDay.add(t);
                        }
                        continue;
                    }
                    if (!d.isBefore(toLocalDate(start)) && !d.isAfter(toLocalDate(end))) {
                        // naive include (doesn't evaluate repeat interval matching — you can extend)
                        tasksForDay.add(t);
                    }
                }
                dayModels.add(new CalendarDayModel(d, tasksForDay));
            }

            requireActivity().runOnUiThread(() -> {
                CalendarDayAdapter adapter = new CalendarDayAdapter(dayModels, categories, (selectedDate, tasksForThatDay) -> {
                    // When a day is clicked -> open TaskListFragment with filtered tasks
                    // Reuse your TaskListFragment.newInstance(List<Task>, List<Category>, boolean showRepeating)
                    TaskListFragment frag = TaskListFragment.newInstance(tasksForThatDay, categories, false);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .addToBackStack(null)
                            .commit();
                });
                recyclerDays.setAdapter(adapter);
            });
        });
    }

    // builds list of LocalDate that will fill the grid for the month (including leading/trailing days)
    private List<LocalDate> buildMonthGridDays(YearMonth ym) {
        List<LocalDate> out = new ArrayList<>();
        LocalDate first = ym.atDay(1);
        int firstWeekDay = first.getDayOfWeek().getValue(); // 1 = Monday ... 7 = Sunday
        // if you want week start Sunday adapt accordingly
        int leading = firstWeekDay - 1; // days from previous month

        // start date = first.minusDays(leading)
        LocalDate start = first.minusDays(leading);
        int totalCells = 42; // 6 weeks * 7 columns ensures full month grid
        for (int i = 0; i < totalCells; i++) {
            out.add(start.plusDays(i));
        }
        return out;
    }

    private boolean sameLocalDate(Date d, LocalDate ld) {
        LocalDate from = toLocalDate(d);
        return from.equals(ld);
    }

    private LocalDate toLocalDate(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    // small model class
    static class CalendarDayModel {
        LocalDate date;
        List<Task> tasks;
        CalendarDayModel(LocalDate date, List<Task> tasks) {
            this.date = date;
            this.tasks = tasks;
        }
    }
}
