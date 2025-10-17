package com.example.rpg.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.rpg.R;
import com.example.rpg.databinding.FragmentRegistrationBinding;

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
            Toast.makeText(requireContext(), "Successfully registered.", Toast.LENGTH_SHORT).show();
        });
    }
}