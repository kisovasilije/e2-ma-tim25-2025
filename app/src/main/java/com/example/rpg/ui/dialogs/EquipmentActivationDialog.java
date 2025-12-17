package com.example.rpg.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.repository.UserEquipmentRepository;
import com.example.rpg.databinding.EquipmentActivationDialogBinding;
import com.example.rpg.model.ActivityStatus;
import com.example.rpg.model.User;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.model.equipment.EquipmentType;
import com.example.rpg.model.equipment.Weapon;
import com.example.rpg.ui.adapters.UserEquipmentAdapter;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EquipmentActivationDialog extends Dialog {
    private static final String TAG = EquipmentActivationDialog.class.getSimpleName();

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

        binding.confirmButton.setOnClickListener(v -> {
            dismiss();

            NavController nav =
                    Navigation.findNavController(
                            requireActivity,
                            R.id.nav_host
                    );

            NavOptions opts = new NavOptions.Builder()
                    .setPopUpTo(R.id.base_navigation, true)
                    .build();

            nav.navigate(R.id.nav_battle, null, opts);
        });

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
        adapter = new UserEquipmentAdapter(getContext(), activate, this::onClick, null);
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
            adapter = new UserEquipmentAdapter(getContext(), activate, this::onClick, null);
        } else if (checkedId == R.id.activated_equipment) {
            Log.d("Dialog", "activated size = " + activated.size());
            adapter = new UserEquipmentAdapter(getContext(), activated, this::onClick, this::onUpgrade);
        }

        binding.equipments.setAdapter(adapter);
    }

    private void onUpgrade(UserEquipment e, int pos, View row) {
        if (e.equipment.getType() != EquipmentType.WEAPON) {
            return;
        }

        e.equipment.addBonus(1);
        executor.execute(() -> {
            var rowsAffected = db.weaponDao().update((Weapon) e.equipment);
            if (rowsAffected < 1) {
                Log.e(TAG, "onUpgrade: Weapon not upgraded");
            }

            Log.i(TAG, "onUpgrade: Weapon upgraded.");
            requireActivity.runOnUiThread(() -> {
                Toast.makeText(getContext(),
                        "Weapon upgraded.",
                        Toast.LENGTH_SHORT
                ).show();
            });
        });
    }
}
