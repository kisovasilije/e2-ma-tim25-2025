package com.example.rpg.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.databinding.FragmentRegistrationBinding;
import com.example.rpg.model.Avatar;
import com.example.rpg.model.User;
import com.example.rpg.model.UserProgress;
import com.example.rpg.ui.activities.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;

    private AppDatabase db;

    public RegistrationFragment() {
        // Required empty public constructor
    }

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
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerAvatarSpinner();

        registerRegisterButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void registerAvatarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.avatars,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.avatarInput.setAdapter(adapter);
    }

    private void registerRegisterButton() {
        binding.registerButton.setOnClickListener(v -> {
            var user = cvtBindingToUser(v);
            if (user == null) return;

            new Thread(() -> {
                var userId = db.userDao().insert(user);
                var progress = UserProgress.getDefault(userId);
                db.userProgressDao().insert(progress);

                var nav = NavHostFragment.findNavController(this);
                var opts = new NavOptions.Builder()
                        .setPopUpTo(R.id.base_navigation, true)
                        .build();

                requireActivity().runOnUiThread(() -> {
                    nav.navigate(R.id.nav_home, null, opts);

                    Toast.makeText(
                            requireContext(),
                            String.format(Locale.US, "User %d successfully registered.", userId),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }).start();
        });
    }

    private User cvtBindingToUser(View v) {
        var email = binding.emailInput.getText().toString();
        var username = binding.usernameInput.getText().toString();
        var password = binding.passwordInput.getText().toString();
        var confirmPassword = binding.confirmPasswordInput.getText().toString();
        var avatar = binding.avatarInput.getSelectedItem().toString();

        var areRequiredFieldsEmpty = email.isEmpty()
                || username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()
                || avatar.isEmpty();

        if (areRequiredFieldsEmpty) {
            Snackbar
                    .make(v, "Must fill required fields.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();

            return null;
        }

        if (!password.equals(confirmPassword)) {
            Snackbar
                    .make(v, "Password mismatch.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();

            return null;
        }

        Avatar a;
        var parsedAvatar = avatar.replace(" ", "").toUpperCase();

        try {
            a = Avatar.valueOf(parsedAvatar);
        }
        catch (Exception e) {
            Snackbar
                    .make(v, (e.getMessage() != null) ? e.getMessage() : "Error occurred selecting avatar.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();

            return null;
        }

        return new User(email, username, password, a);
    }
}