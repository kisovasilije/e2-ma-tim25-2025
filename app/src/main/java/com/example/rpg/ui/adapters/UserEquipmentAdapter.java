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
import com.example.rpg.model.ActivityStatus;
import com.example.rpg.model.UserEquipment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentAdapter extends ArrayAdapter<UserEquipment> {
    public interface UeOnAction {
        void onClick(UserEquipment e, int pos, View row);
    }

    private final UeOnAction onAction;

    private final List<UserEquipment> data = new ArrayList<>();

    public UserEquipmentAdapter(
            @NonNull Context context,
            @NonNull List<UserEquipment> data,
            @NonNull UeOnAction onAction
    ) {
        super(context, 0, data);

        this.onAction = onAction;
        this.data.addAll(data);
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

        TextView equipmentName = v.findViewById(R.id.equipment_name_text);
        TextView equipmentType = v.findViewById(R.id.equipment_type_text);
        TextView equipmentDescription = v.findViewById(R.id.equipment_description_text);
        Button btn = v.findViewById(R.id.activate_equipment_button);

        equipmentName.setText(String.format("%s", e.equipment.getName()));
        equipmentType.setText(String.format("%s", e.equipment.getType().toString()));
        equipmentDescription.setText(e.equipment.getDescription());

        if (e.status != ActivityStatus.PURCHASED) {
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

    public void updateData(List<UserEquipment> ues) {
        data.clear();
        data.addAll(ues);
        notifyDataSetChanged();
    }
}
