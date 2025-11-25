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
import com.example.rpg.model.equipment.Equipment;

import java.util.List;

public class EquipmentAdapter extends ArrayAdapter<Equipment> {
    public interface OnAction {
        void onClick(Equipment e, int pos, View row);
    }

    private final EquipmentAdapter.OnAction onAction;

    public EquipmentAdapter(
            @NonNull Context context,
            @NonNull List<Equipment> data,
            @NonNull EquipmentAdapter.OnAction onAction
    ) {
        super(context, 0, data);

        this.onAction = onAction;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.equipment_row, parent, false);
        }

        Equipment e = getItem(position);
        if (e == null) {
            return convertView;
        }

        TextView name = convertView.findViewById(R.id.equipment_name_text);
        TextView type = convertView.findViewById(R.id.equipment_type_text);
        Button btn = convertView.findViewById(R.id.buy_equipment_button);

        name.setText(String.format("%s", e.getName()));
        type.setText(String.format("%s", e.getType()));

        final View row = convertView;
        btn.setOnClickListener(view -> onAction.onClick(e, position, row));

        return convertView;
    }
}
