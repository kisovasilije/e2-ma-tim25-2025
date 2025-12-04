package com.example.rpg.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.repository.UserEquipmentRepository;
import com.example.rpg.databinding.EquipmentActivationDialogBinding;
import com.example.rpg.model.ActivityStatus;
import com.example.rpg.model.User;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.ui.adapters.UserEquipmentAdapter;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EquipmentActivationDialog extends Dialog {
    private EquipmentActivationDialogBinding binding;

    private UserEquipmentAdapter adapter;

    private List<UserEquipment> activate;

    private List<UserEquipment> activated;

    private User user;

    private final FragmentActivity requireActivity;

    private final AppDatabase db;

    private final UserEquipmentRepository ueRepository;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public EquipmentActivationDialog(
            @NonNull Context context,
            User user,
            FragmentActivity requireActivity
    ) {
        super(context);

        db = AppDatabase.get(context.getApplicationContext());
        this.user = user;
        this.requireActivity = requireActivity;
        this.ueRepository = new UserEquipmentRepository(db);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = EquipmentActivationDialogBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.activateEquipment.setChecked(true);

        executor.execute(() -> {
            var ues = ueRepository.getAllWithEquipmentByUserId(user.id);
            activate = ues.stream()
                    .filter(e -> e.status == ActivityStatus.PURCHASED)
                    .collect(Collectors.toList());

            activated = ues.stream()
                    .filter(e ->
                            e.status != ActivityStatus.PURCHASED &&
                            e.status != ActivityStatus.USED)
                    .collect(Collectors.toList());

            requireActivity.runOnUiThread(() -> {
                this.setupAdapter();
                binding.equipmentRadioGroup.setOnCheckedChangeListener(this::onEquipmentCollectionChanged);
            });

        });
    }

    private void setupAdapter() {
        adapter = new UserEquipmentAdapter(getContext(), activate, this::onClick);
        binding.equipments.setAdapter(adapter);
    }

    private void onClick(UserEquipment e, int pos, View row) {
        e.updateStatus();
        executor.execute(() -> {
            var rowsAffected = db.userEquipmentDao().update(e);
            if (rowsAffected < 1) {
                Log.w("[Dialog]", "User equipment status not updated properly.");
                return;
            }

            Log.d("[Dialog]", "User equipment status updated properly.");
            activate.remove(e);
            activated.add(e);

            requireActivity.runOnUiThread(() -> {
                Toast.makeText(getContext(),
                        "Equipment activated.",
                        Toast.LENGTH_SHORT
                ).show();

                adapter.notifyDataSetChanged();
            });
        });
    }

    private void onEquipmentCollectionChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.activate_equipment) {
            Log.d("Dialog", "activate size = " + activate.size());
            adapter = new UserEquipmentAdapter(getContext(), activate, this::onClick);
        } else if (checkedId == R.id.activated_equipment) {
            Log.d("Dialog", "activated size = " + activated.size());
            adapter = new UserEquipmentAdapter(getContext(), activated, this::onClick);
        }

        binding.equipments.setAdapter(adapter);
    }
}
