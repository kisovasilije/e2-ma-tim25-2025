package com.example.rpg.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
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
import com.example.rpg.model.Boss;
import com.example.rpg.model.UserProgress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleFragment extends Fragment {

    private TextView tvBossHP, tvPlayerPP, tvAttackChance, tvAttacksLeft;
    private Button btnAttack;
    private TaskDao taskDao;
    private Boss currentBoss;
    private UserProgress progress;
    private LottieAnimationView lottieResult;
    private TextView tvResultText;

    private int attacksLeft = 5;
    private double successRate = 0.0;
    private int bossOriginalHP;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        executor.execute(() -> {
            taskDao = AppDatabase.get(requireContext()).taskDao();

            progress = AppDatabase.get(requireContext()).userProgressDao().getById(1);

            currentBoss = AppDatabase.get(requireContext())
                    .bossDao()
                    .getCurrentBoss(progress.level);

            if (currentBoss == null) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "No more bosses left!", Toast.LENGTH_LONG).show();
                    navigateToTasks();
                });
                return;
            }

            bossOriginalHP = currentBoss.hp;
            successRate = progress.getSuccessRate(taskDao, progress.level);
            requireActivity().runOnUiThread(this::updateUI);
        });
    }

    private void updateUI() {
        if (progress == null || currentBoss == null) return;

        tvBossHP.setText("Boss HP: " + currentBoss.hp);
        tvPlayerPP.setText("Your PP: " + progress.pp);
        tvAttackChance.setText("Attack chance: " + String.format("%.0f", successRate) + "%");
        tvAttacksLeft.setText("Attacks left: " + attacksLeft);
        btnAttack.setEnabled(attacksLeft > 0);
    }

    private void performAttack() {
        if (attacksLeft <= 0 || progress == null || currentBoss == null) return;

        attacksLeft--;
        boolean hit = Math.random() * 100 < successRate;

        if (hit) {
            currentBoss.hp -= progress.pp;
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
            progress.coins += currentBoss.rewardCoins;
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
        executor.execute(() -> AppDatabase.get(requireContext()).userProgressDao().update(progress));
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
}