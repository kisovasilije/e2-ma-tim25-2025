package com.example.rpg.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.rpg.R;

public class EquipmentActivationDialog extends Dialog {


    public EquipmentActivationDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.equipment_activation_dialog);
    }
}
