package com.example.rpg.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.database.managers.ProgressManager;
import com.example.rpg.database.repository.UserEquipmentRepository;
import com.example.rpg.model.Boss;
import com.example.rpg.model.UserEquipment;
import com.example.rpg.model.UserProgress;
import com.example.rpg.model.equipment.EquipmentType;
import com.example.rpg.prefs.AuthPrefs;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleFragment extends Fragment {
    private static final String TAG = BattleFragment.class.getSimpleName();

    private TextView tvBossHP, tvPlayerPP, tvAttackChance, tvAttacksLeft;
    private Button btnAttack;
    private TaskDao taskDao;
    private Boss currentBoss;
    private UserProgress progress;
    private LottieAnimationView lottieResult;
    private TextView tvResultText;
    private ProgressManager progressManager;
    private int attacksLeft = 5;
    private double successRate = 0.0;
    private int bossOriginalHP;

    private int damage;

    private int rewardCoins;

    private AppDatabase db;

    private UserEquipmentRepository userEquipmentRepository;

    private List<UserEquipment> activatedEquipment;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        db = AppDatabase.get(context.getApplicationContext());
        userEquipmentRepository = new UserEquipmentRepository(db);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle, container, false);

        tvBossHP = view.findViewById(R.id.tv_boss_hp);
        tvPlayerPP = view.findViewById(R.id.tv_player_pp);
        tvAttackChance = view.findViewById(R.id.tv_attack_chance);
        tvAttacksLeft = view.findViewById(R.id.tv_attacks_left);
        btnAttack = view.findViewById(R.id.btn_attack);

        // new views
        lottieResult = view.findViewById(R.id.lottie_result);
        tvResultText = view.findViewById(R.id.tv_result_text);

        loadPlayerAndBoss();

        btnAttack.setOnClickListener(v -> performAttack());

        return view;
    }

    private void loadPlayerAndBoss() {
        var username = AuthPrefs.getIsAuthenticated(requireContext());
        if (username == null || username.isBlank()) {
            Log.w(TAG, "init: user not logged in.");
            return;
        }

        executor.execute(() -> {
            var user = db.userDao().getByUsername(username);
            if (user == null) {
                Log.e(TAG, "init: user doesn't exist.");
                return;
            }

            taskDao = AppDatabase.get(requireContext()).taskDao();

            progressManager = new ProgressManager(taskDao);

            progress = AppDatabase.get(requireContext()).userProgressDao().getById(user.id);

            currentBoss = AppDatabase.get(requireContext())
                    .bossDao()
                    .getCurrentBoss(progress.level);

            activatedEquipment = userEquipmentRepository.getActivatedWithEquipmentByUserId(user.id);

            if (currentBoss == null) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "No more bosses left!", Toast.LENGTH_LONG).show();
                    navigateToTasks();
                });
                return;
            }

            bossOriginalHP = currentBoss.hp;
            successRate = progressManager.getSuccessRate(progress.id, progress.level);
            damage = progress.pp;
            rewardCoins = currentBoss.rewardCoins;

            boost();
            requireActivity().runOnUiThread(this::updateUI);
        });
    }

    private void updateUI() {
        if (progress == null || currentBoss == null) return;

        tvBossHP.setText("Boss HP: " + currentBoss.hp);
        tvPlayerPP.setText("Your PP: " + damage);
        tvAttackChance.setText("Attack chance: " + String.format("%.0f", successRate) + "%");
        tvAttacksLeft.setText("Attacks left: " + attacksLeft);
        btnAttack.setEnabled(attacksLeft > 0);
    }

    private void performAttack() {
        if (attacksLeft <= 0 || progress == null || currentBoss == null) return;

        attacksLeft--;
        boolean hit = Math.random() * 100 < successRate;

        if (hit) {
            currentBoss.hp -= damage;
            if (currentBoss.hp < 0) currentBoss.hp = 0;
        }

        requireActivity().runOnUiThread(() -> {
            updateUI();

            if (attacksLeft == 0 || currentBoss.hp == 0) {
                showBattleResult();
            }
        });
    }

    private void showBattleResult() {
        if (currentBoss.hp == 0) {
            progress.coins += rewardCoins;
            progress.level += 1;
            progress.pp = (int) (progress.pp * 1.75);
            playResultAnimation(true,
                    "Boss defeated! Coins +" + currentBoss.rewardCoins +
                            ", Level up to " + progress.level +
                            ", PP now " + progress.pp);
        } else {
            int damageDealt = bossOriginalHP - currentBoss.hp;
            double damagePercent = (double) damageDealt / bossOriginalHP;

            if (damagePercent >= 0.5) {
                int coinsEarned = (int) (currentBoss.rewardCoins * 0.5);
                progress.coins += coinsEarned;
                progress.pp += (int) (progress.pp * 0.3);

                playResultAnimation(true,
                        "Boss survived! Partial reward: Coins +" + coinsEarned +
                                ", PP +30%");
            } else {
                playResultAnimation(false, "Boss survived! No reward.");
            }
        }

        // Check if working
        executor.execute(this::updateAfterBattle);
    }

    private void playResultAnimation(boolean victory, String message) {
        requireActivity().runOnUiThread(() -> {
            lottieResult.setVisibility(View.VISIBLE);
            tvResultText.setVisibility(View.VISIBLE);
            tvResultText.setText(message);

            if (victory) {
                lottieResult.setAnimation(R.raw.victory_animation);
            } else {
                lottieResult.setAnimation(R.raw.failure_animation);
            }

            lottieResult.playAnimation();

            lottieResult.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieResult.setVisibility(View.GONE);
                    tvResultText.setVisibility(View.GONE);
                    navigateToTasks();
                }
            });
        });
    }

    private void navigateToTasks() {
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_battle, new TaskFragment())
//                .commit();
    }

    private void boost() {
        double
                newDamage = damage,
                newAttacksLeft = attacksLeft,
                newRewardCoins = rewardCoins;

        for (var e : activatedEquipment) {
            var bonus = e.equipment.getBonus();
            var subType = e.equipment.getSubType();

            switch (subType) {
                case "shield":
                    successRate = applyPercent(successRate, bonus);
                    break;

                case "boots":
                    newAttacksLeft = applyPercent(newAttacksLeft, bonus);
                    break;

                case "bow_and_arrow":
                    newRewardCoins = applyPercent(newRewardCoins, bonus);
                    break;

                default:
                    newDamage = applyPercent(newDamage, bonus);
                    break;
            }
        }

        attacksLeft = (int) Math.round(newAttacksLeft);
        rewardCoins = (int) Math.round(newRewardCoins);
        damage = (int) Math.round(newDamage);
    }

    private double applyPercent(double base, double percent) {
        return base + (base * percent / 100.0);
    }

    private void updateAfterBattle() {
        var rowsAffected = db.userProgressDao().update(progress);
        if (rowsAffected < 1) {
            Log.w(TAG, "updateAfterBattle: User progress not updated after battle finish.");
            return;
        }

        var ueStatusUpdated = true;
        for (var e : activatedEquipment) {
            e.updateStatus();
            rowsAffected = db.userEquipmentDao().update(e);
            if (rowsAffected < 1) {
                Log.w(TAG, "updateAfterBattle: " + e.equipment.getName() + " status not updated after battle finish.");
                ueStatusUpdated = false;
                break;
            }
        }

        if (ueStatusUpdated) {
            printOnUi("Game finished.");
        } else {
            printOnUi("Error finishing game.");
        }
    }

    private void printOnUi(String msg) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(
                    requireContext(),
                    msg,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}