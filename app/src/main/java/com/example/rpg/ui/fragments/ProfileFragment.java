package com.example.rpg.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.repository.UserEquipmentRepository;
import com.example.rpg.databinding.FragmentProfileBinding;
import com.example.rpg.model.ActivityStatus;
import com.example.rpg.model.User;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.model.UserProgress;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.ui.activities.MainActivity;
import com.example.rpg.ui.adapters.UserEquipmentAdapter;
import com.example.rpg.ui.dialogs.PreviewEquipmentDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private AppDatabase db;

    private User user;

    private UserProgress progress;

    private List<UserEquipment> activated;

    private List<UserEquipment> purchased;

    private UserEquipmentRepository userEquipmentRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        db = AppDatabase.get(context.getApplicationContext());
        userEquipmentRepository = new UserEquipmentRepository(db);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUser();

        registerListeners();
    }

    private void registerListeners() {
        binding.resetPasswordButton.setOnClickListener(this::toggleResetPassword);

        binding.confirmResetPasswordButton.setOnClickListener(this::confirmNewPassword);

        binding.showActivatedEquipmentButton.setOnClickListener((v) -> {
            new PreviewEquipmentDialog(requireContext(), activated, requireActivity()).show();
        });

        binding.showPurchasedEquipmentButton.setOnClickListener((v) -> {
            new PreviewEquipmentDialog(requireContext(), purchased, requireActivity()).show();
        });
    }

    private void setUser() {
        var username = AuthPrefs.getIsAuthenticated(requireContext());
        Log.i("USERNAME", username != null ? username : "NO USERNAME");

        new Thread(() -> {
            user = db.userDao().getByUsername(username);
            if (user != null) {
                progress = db.userProgressDao().getById(user.id);
                var ues = userEquipmentRepository.getAllWithEquipmentByUserId(user.id);
                purchased = ues.stream()
                        .filter(e -> e.status == ActivityStatus.PURCHASED)
                        .collect(Collectors.toList());

                activated = ues.stream()
                        .filter(e ->
                                e.status != ActivityStatus.PURCHASED &&
                                        e.status != ActivityStatus.USED)
                        .collect(Collectors.toList());
            }

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    setProfile();
                    return;
                }

                if(getContext() == null) return;

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setAuth(true);
                }

                var nav = NavHostFragment.findNavController(this);
                var opts = new NavOptions.Builder()
                        .setPopUpTo(R.id.base_navigation, true)
                        .build();

                nav.navigate(R.id.nav_login, null, opts);
            });
        }).start();
    }

    private void setProfile() {
        binding.avatarText.setText(user.avatar.toString().toLowerCase());
        binding.usernameText.setText(user.username);
        binding.levelText.setText(String.format("%d", progress.level));
        binding.titleText.setText(String.format("%s", progress.title));
        binding.ppText.setText(String.format("%d", progress.pp));
        binding.xpText.setText(String.format("Xp: %d", progress.xp));
        binding.coinsText.setText(String.format("Coins: %d", progress.coins));
    }

    private void toggleResetPassword(View v) {
        var currentVisibility = binding.resetPasswordForm.getVisibility();
        if (currentVisibility == View.GONE)
            binding.resetPasswordForm.setVisibility(View.VISIBLE);
        else
            binding.resetPasswordForm.setVisibility(View.GONE);
    }

    private void confirmNewPassword(View v) {
        var currentPassword = binding.currentPassword.getText().toString();
        var newPassword = binding.newPassword.getText().toString();
        var confirmNew = binding.confirmNewPassword.getText().toString();

        var areRequiredFieldsEmpty = currentPassword.isEmpty()
                || newPassword.isEmpty()
                || confirmNew.isEmpty();

        var passwordsMatches = newPassword.equals(confirmNew);

        if (areRequiredFieldsEmpty || !passwordsMatches) {
            Snackbar
                    .make(v, "Validation error.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();

            return;
        }

        new Thread(() -> {
            if (!user.password.equals(currentPassword)) {
                Snackbar
                        .make(v, "Invalid current password.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show();

                return;
            }

            var userUpdate = new User(user.id, user.email, user.username, user.password, user.avatar);
            userUpdate.password = newPassword;
            db.userDao().update(userUpdate);

            requireActivity().runOnUiThread(() -> binding.resetPasswordForm.setVisibility(View.GONE));
        }).start();
    }
}