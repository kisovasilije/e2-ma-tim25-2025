package com.example.rpg.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.rpg.databinding.PreviewEquipmentDialogBinding;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.ui.adapters.ReadonlyUeAdapter;

import java.util.ArrayList;
import java.util.List;

public class PreviewEquipmentDialog extends Dialog {
    private PreviewEquipmentDialogBinding binding;

    private ReadonlyUeAdapter adapter;

    private List<UserEquipment> equipments;

    private FragmentActivity requireActivity;

    public PreviewEquipmentDialog(
            @NonNull Context context,
            List<UserEquipment> equipments,
            FragmentActivity requireActivity) {
        super(context);

        this.equipments = new ArrayList<>(equipments);
        this.requireActivity = requireActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = PreviewEquipmentDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        requireActivity.runOnUiThread(this::setupAdapter);
        binding.closeDialogButton.setOnClickListener((v) -> {
            this.dismiss();
        });
    }

    private void setupAdapter() {
        adapter = new ReadonlyUeAdapter(getContext(), this.equipments);
        binding.equipments.setAdapter(adapter);
    }
}
