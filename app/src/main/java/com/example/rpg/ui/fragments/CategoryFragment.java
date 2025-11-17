package com.example.rpg.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.CategoryDao;
import com.example.rpg.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryFragment extends Fragment {

    private AppDatabase db;
    private CategoryDao categoryDao;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Category> categories = new ArrayList<>();
    private FloatingActionButton fabAdd;

    private Map<String, Integer> colorMap = new HashMap<>();

    public CategoryFragment() {
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Blue", Color.BLUE);
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Cyan", Color.CYAN);
        colorMap.put("Magenta", Color.MAGENTA);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = AppDatabase.get(context.getApplicationContext());
        categoryDao = db.categoryDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        listView = view.findViewById(R.id.category_list);
        fabAdd = view.findViewById(R.id.fab_add_category);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> showCategoryDialog(categories.get(position)));
        listView.setOnItemLongClickListener((parent, v, position, id) -> {
            deleteCategory(categories.get(position));
            return true;
        });

        fabAdd.setOnClickListener(v -> showCategoryDialog(null));

        loadCategories();

        return view;
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> list = categoryDao.getAll();
            requireActivity().runOnUiThread(() -> {
                categories.clear();
                categories.addAll(list);

                adapter.clear();

                for (Category c : categories) {
                    String name = c.name;
                    adapter.add(name);
                }

                listView.post(() -> {
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View item = listView.getChildAt(i);
                        Category category = categories.get(i);
                        int colorWithOpacity = (category.color & 0x00FFFFFF) | (0x4D << 24);
                        item.setBackgroundColor(colorWithOpacity);
                    }
                });
                adapter.notifyDataSetChanged();

            });
        });
    }
    @Nullable
    private void showCategoryDialog(@Nullable Category category) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category, null);
        builder.setView(dialogView);

        EditText inputName = dialogView.findViewById(R.id.input_category_name);
        View colorPreview = dialogView.findViewById(R.id.view_color_preview);
        View btnPickColor = dialogView.findViewById(R.id.btn_pick_color);

        final int[] selectedColor = new int[1];
        selectedColor[0] = (category != null) ? category.color : Color.RED;

        colorPreview.setBackgroundColor(selectedColor[0]);

        btnPickColor.setOnClickListener(v -> {
            AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(
                    requireContext(),
                    selectedColor[0],
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            selectedColor[0] = color;
                            colorPreview.setBackgroundColor(color);
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {}
                    }
            );
            colorDialog.show();
        });

        if (category != null) {
            inputName.setText(category.name);
        }

        builder.setTitle(category == null ? "Add Category" : "Edit Category");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = inputName.getText().toString().trim();

            if (name.isEmpty()) {
                Snackbar.make(listView, "Name cannot be empty", Snackbar.LENGTH_SHORT).show();
                return;
            }

            int chosenColor = selectedColor[0];

            Executors.newSingleThreadExecutor().execute(() -> {

                Category existing = categoryDao.getByColor(chosenColor);
                if (existing != null && (category == null || existing.id != category.id)) {
                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(listView, "Color already in use", Snackbar.LENGTH_SHORT).show()
                    );
                    return;
                }

                if (category == null) {
                    categoryDao.insert(new Category(name, chosenColor));
                } else {
                    category.name = name;
                    category.color = chosenColor;
                    categoryDao.update(category);
                }

                loadCategories();
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void deleteCategory(Category category) {
        Executors.newSingleThreadExecutor().execute(() -> {
            categoryDao.delete(category);

            requireActivity().runOnUiThread(() -> {

                int index = categories.indexOf(category);
                if (index >= 0) {
                    categories.remove(index);
                    adapter.remove(adapter.getItem(index));
                }


                listView.post(() -> {
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View item = listView.getChildAt(i);
                        Category c = categories.get(i);
                        int colorWithOpacity = (c.color & 0x00FFFFFF) | (0x4D << 24);
                        item.setBackgroundColor(colorWithOpacity);
                    }
                });

                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }


    private String getColorName(int colorInt) {
        for (Map.Entry<String, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() == colorInt) return entry.getKey();
        }
        return "Unknown";
    }
}