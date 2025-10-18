package com.example.rpg.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

            AppDatabase db = Room.databaseBuilder(
                    requireContext().getApplicationContext(),
                    AppDatabase.class,
                    AppDatabase.databaseName
            ).build();

            new Thread(() -> {
                var userId = db.userDao().insert(user);

                requireActivity().runOnUiThread(() -> {
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
        var password = binding.passwordInput.getText().toString();
        if (!password.equals(binding.confirmPasswordInput.getText().toString())) {
            Snackbar
                    .make(v, "Password mismatch.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();

            return null;
        }

        Avatar a;
        var in = binding.avatarInput.getSelectedItem().toString();
        var parsedAvatar = in.replace(" ", "").toUpperCase();

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

        return new User(
                binding.emailInput.getText().toString(),
                binding.usernameInput.getText().toString(),
                password,
                a
        );
    }
}