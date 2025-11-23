//package com.example.rpg.ui.fragments;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.example.rpg.R;
//import com.example.rpg.database.AppDatabase;
//import com.example.rpg.databinding.FragmentShopBinding;
//import com.example.rpg.model.Equipment;
//import com.example.rpg.model.User;
//import com.example.rpg.model.UserEquipment;
//import com.example.rpg.prefs.AuthPrefs;
//import com.example.rpg.ui.adapters.EquipmentAdapter;
//
//import java.util.Locale;
//
///**
// * A simple {@link Fragment} subclass.
// * create an instance of this fragment.
// */
//public class ShopFragment extends Fragment {
//    private FragmentShopBinding binding;
//
//    private AppDatabase db;
//
//    private EquipmentAdapter adapter;
//
//    private User user;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        db = AppDatabase.get(context.getApplicationContext());
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentShopBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        init();
//    }
//
//    private void init() {
//        var username = AuthPrefs.getIsAuthenticated(requireContext());
//
//        new Thread(() -> {
//            user = db.userDao().getByUsername(username);
//            var equipments = db.equipmentDao().getAll();
//
//            requireActivity().runOnUiThread(() -> {
//                ListView lw = binding.equipments;
//
//                adapter = new EquipmentAdapter(requireContext(), equipments, this::onAction);
//                lw.setAdapter(adapter);
//            });
//        }).start();
//    }
//
//    private void onAction(Equipment e, int pos, View row) {
//        new Thread(() -> {
//            var userEq = new UserEquipment(user.id, e.id, false);
//            db.userEquipmentDao().insert(userEq);
//
//            requireActivity().runOnUiThread(() -> {
//                Toast.makeText(
//                        requireContext(),
//                        String.format(Locale.US, "Purchased."),
//                        Toast.LENGTH_SHORT
//                ).show();
//            });
//        }).start();
//    }
//}