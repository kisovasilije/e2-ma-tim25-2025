package com.example.rpg.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.databinding.FragmentAuthBinding;
import com.example.rpg.prefs.AuthPrefs;
import com.example.rpg.ui.activities.MainActivity;
import com.example.rpg.ui.dtos.LoginCredentialsDto;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;

    private AppDatabase db;

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
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerLoginButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void registerLoginButton() {
        binding.loginButton.setOnClickListener(v -> {
            var loginCreds = cvtBindingToLoginCreds();
            if (loginCreds == null) {
                Snackbar
                        .make(v, "Must fill required fields.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show();

                return;
            }

            new Thread(() -> {
                var user = db.userDao().getByUsername(loginCreds.username);
                if (user == null) {
                    Snackbar
                            .make(v, "User doesn't exist.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .show();

                    return;
                }

                if (!user.password.equals(loginCreds.password)) {
                    Snackbar
                            .make(v, "Wrong password.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .show();

                    return;
                }

                AuthPrefs.setUser(requireContext(), user.username);

                requireActivity().runOnUiThread(() -> {
                    if(getContext() == null) return;

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setAuth(true);
                    }

                    var nav = NavHostFragment.findNavController(this);
                    var opts = new NavOptions.Builder()
                            .setPopUpTo(R.id.base_navigation, true)
                            .build();

                    nav.navigate(R.id.nav_home, null, opts);

                    Toast.makeText(
                            requireContext(),
                            String.format(Locale.US, "User %s successfully authenticated.", user.username),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }).start();
        });
    }

    private LoginCredentialsDto cvtBindingToLoginCreds() {
        var username = binding.usernameInput.getText().toString();
        var password = binding.passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) return null;

        return new LoginCredentialsDto(username, password);
    }
}