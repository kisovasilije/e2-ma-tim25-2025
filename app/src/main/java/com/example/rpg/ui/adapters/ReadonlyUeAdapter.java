package com.example.rpg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rpg.R;
import com.example.rpg.model.UserEquipment;

import java.util.List;

public class ReadonlyUeAdapter extends ArrayAdapter<UserEquipment> {
    public ReadonlyUeAdapter(
            @NonNull Context context,
            @NonNull List<UserEquipment> data
    ) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.readonly_ue_row, parent, false);
        }

        UserEquipment e = getItem(position);
        if (e == null) return v;

        TextView equipmentName = v.findViewById(R.id.equipment_name_text);
        TextView equipmentType = v.findViewById(R.id.equipment_type_text);
        TextView equipmentDescription = v.findViewById(R.id.equipment_description_text);

        equipmentName.setText(String.format("%s", e.equipment.getName()));
        equipmentType.setText(String.format("%s", e.equipment.getType().toString()));
        equipmentDescription.setText(e.equipment.getDescription());

        return v;
    }
}
