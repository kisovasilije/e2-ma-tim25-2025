package com.example.rpg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rpg.R;
import com.example.rpg.model.Equipment;

import java.util.List;

public class EquipmentAdapter extends ArrayAdapter<Equipment> {
    public interface EquipmentOnAction {
        void onClick(Equipment e, int post, View row);
    }

    private final EquipmentOnAction onAction;

    public EquipmentAdapter(
            @NonNull Context context,
            @NonNull List<Equipment> data,
            @NonNull EquipmentOnAction onAction
    ) {
        super(context, 0, data);

        this.onAction = onAction;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.equipment_row, parent, false);
        }

        Equipment e = getItem(position);
        if (e == null) return v;

        return v;
    }
}
