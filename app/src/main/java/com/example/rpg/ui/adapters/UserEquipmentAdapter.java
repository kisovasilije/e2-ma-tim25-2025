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
import com.example.rpg.model.Equipment;
import com.example.rpg.model.UserEquipment;

import java.util.List;

public class UserEquipmentAdapter extends ArrayAdapter<UserEquipment> {
    public interface UeOnAction {
        void onClick(UserEquipment e, int pos, View row);
    }

    private final UeOnAction onAction;

    public UserEquipmentAdapter(
            @NonNull Context context,
            @NonNull List<UserEquipment> data,
            @NonNull UeOnAction onAction
    ) {
        super(context, 0, data);

        this.onAction = onAction;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.ue_row, parent, false);
        }

        UserEquipment e = getItem(position);
        if (e == null) return v;

        TextView userId = v.findViewById(R.id.user_id_text);
        TextView equipmentId = v.findViewById(R.id.equipment_id_text);
        Button btn = v.findViewById(R.id.activate_equipment_button);

        userId.setText(String.format("User ID: %s", e.userId));
        equipmentId.setText(String.format("Eq ID: %s", e.equipmentId));

        if (e.isActivated) {
            btn.setEnabled(false);
            btn.setText("Activated");
        }
        else {
            final View row = v;
            btn.setEnabled(true);
            btn.setOnClickListener(view -> onAction.onClick(e, position, row));
        }

        return v;
    }
}
