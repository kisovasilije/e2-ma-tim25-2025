/*
package com.example.rpg.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.databinding.FragmentEquipmentBinding;
import com.example.rpg.databinding.FragmentLevelBinding;
import com.example.rpg.model.User;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.model.UserProgress;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.ui.adapters.UserEquipmentAdapter;

import java.util.List;
import java.util.Locale;

*/
/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *//*

public class EquipmentFragment extends Fragment {
    private FragmentEquipmentBinding binding;

    private AppDatabase db;

    private UserEquipmentAdapter adapter;

    private User user;

    private UserProgress progress;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        db = AppDatabase.get(context.getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEquipmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        new Thread(() -> {
            db.userEquipmentDao().deactivateByUserId(user.id);
        }).start();
    }

    private void init() {
        var username = AuthPrefs.getIsAuthenticated(requireContext());

        new Thread(() -> {
            user = db.userDao().getByUsername(username);
            progress = db.userProgressDao().getById(user.id);
            List<UserEquipment> ues = db.userEquipmentDao().getByUserId(user.id);

            requireActivity().runOnUiThread(() -> {
                binding.currentPpText.setText(String.format("%d", progress.pp));
                binding.boostedPpText.setText(String.format("%d", progress.pp));
                ListView lw = binding.userEquipments;

                adapter = new UserEquipmentAdapter(requireContext(), ues, this::onClick);
                lw.setAdapter(adapter);
            });
        }).start();
    }

    private void onClick(UserEquipment e, int pos, View row) {
        new Thread(() -> {
            e.isActivated = true;
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            db.userEquipmentDao().update(e);

            var equipment = db.equipmentDao().getById(e.equipmentId);

            requireActivity().runOnUiThread(() -> {
                binding.boostedPpText.setText(String.format("%d", progress.pp + equipment.ppPct));
                Toast.makeText(
                        requireContext(),
                        String.format(Locale.US, "Activated."),
                        Toast.LENGTH_SHORT
                ).show();
            });
        }).start();
    }
}*/
