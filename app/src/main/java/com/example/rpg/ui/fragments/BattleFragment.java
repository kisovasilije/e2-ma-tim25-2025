package com.example.rpg.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.rpg.model.equipment.Equipment;
import com.example.rpg.model.equipment.EquipmentType;
import com.example.rpg.prefs.AuthPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleFragment extends Fragment {
    private static final String TAG = BattleFragment.class.getSimpleName();

    private TextView tvBossHpLabel, tvPlayerPpLabel, tvAttackChance, tvAttacksLeft;

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
    private ImageView ivBoss, ivHit;
    private AnimationDrawable bossAnim, hitAnim;
    private ProgressBar pbBossHp, pbPlayerPp;
    private int barsMax;
    private boolean bossDefeated = false;
    private AppDatabase db;
    private UserEquipmentRepository userEquipmentRepository;
    private List<UserEquipment> activatedEquipment;
    private List<UserEquipment> activateEquipmentWithDuplicates;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener shakeListener;
    private long lastShakeMs = 0;
    private static final long SHAKE_COOLDOWN_MS = 1900;
    private static final float SHAKE_THRESHOLD_G = 2.2f;
    private FrameLayout overlayRng;
    private TextView tvRng;
    private ImageView ivHitMiss;
    private boolean rngRunning = false;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final Random rng = new Random();


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

        tvBossHpLabel = view.findViewById(R.id.tv_boss_hp_label);
        tvPlayerPpLabel = view.findViewById(R.id.tv_player_pp_label);
        tvAttackChance = view.findViewById(R.id.tv_attack_chance);
        tvAttacksLeft = view.findViewById(R.id.tv_attacks_left);
        btnAttack = view.findViewById(R.id.btn_attack);
        lottieResult = view.findViewById(R.id.lottie_result);
        tvResultText = view.findViewById(R.id.tv_result_text);
        ivBoss = view.findViewById(R.id.iv_boss);
        ivBoss = view.findViewById(R.id.iv_boss);
        ivBoss.post(() -> startBossAnim(R.drawable.anim_boss_idle));
        pbBossHp = view.findViewById(R.id.pb_boss_hp);
        pbPlayerPp = view.findViewById(R.id.pb_player_pp);
        overlayRng = view.findViewById(R.id.overlay_rng);
        tvRng = view.findViewById(R.id.tv_rng);
        ivHitMiss = view.findViewById(R.id.iv_hitmiss);



        ivBoss.post(() -> {
            bossAnim = (AnimationDrawable) ivBoss.getDrawable();
            if (bossAnim != null) bossAnim.start();
        });

        loadPlayerAndBoss();

        btnAttack.setOnClickListener(v -> performAttack());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        shakeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float gX = x / SensorManager.GRAVITY_EARTH;
                float gY = y / SensorManager.GRAVITY_EARTH;
                float gZ = z / SensorManager.GRAVITY_EARTH;

                float gForce = (float) Math.sqrt(gX*gX + gY*gY + gZ*gZ);

                if (gForce > SHAKE_THRESHOLD_G) {
                    long now = System.currentTimeMillis();
                    if (now - lastShakeMs < SHAKE_COOLDOWN_MS) return;
                    lastShakeMs = now;

                    onShakeAttack();
                }
            }

            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null && shakeListener != null) {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    private void onShakeAttack() {
        if (rngRunning) return;
        if (attacksLeft <= 0 || progress == null || currentBoss == null) return;

        if (currentBoss.hp == 0 || attacksLeft == 0) return;

        startRngRevealAndAttack();
    }

    private void startRngRevealAndAttack() {
        rngRunning = true;

        // lock screen / darken
        overlayRng.setVisibility(View.VISIBLE);
        ivHitMiss.setVisibility(View.GONE);
        tvRng.setVisibility(View.VISIBLE);

        // also disable button just in case
        btnAttack.setEnabled(false);

        int finalRoll = rng.nextInt(101); // 0..100

        // roll fake numbers for ~600ms
        long rollDuration = 1600;
        long tick = 50;
        int ticks = (int) (rollDuration / tick);

        for (int i = 0; i < ticks; i++) {
            int fake = rng.nextInt(101);
            ui.postDelayed(() -> tvRng.setText(String.valueOf(fake)), i * tick);
        }

        // show final number
        ui.postDelayed(() -> tvRng.setText(String.valueOf(finalRoll)), rollDuration);

        // immediately replace with HIT/MISS png
        ui.postDelayed(() -> {
            boolean hit = finalRoll < successRate;

            tvRng.setVisibility(View.GONE);
            ivHitMiss.setVisibility(View.VISIBLE);
            ivHitMiss.setImageResource(hit ? R.drawable.hit : R.drawable.miss);

            // now apply the real attack (same as clicking Attack)
            applyAttackFromShake(hit);

        }, rollDuration + 180);

        // hide overlay shortly after
        ui.postDelayed(() -> {
            overlayRng.setVisibility(View.GONE);
            rngRunning = false;

            // re-enable if fight continues
            btnAttack.setEnabled(attacksLeft > 0 && currentBoss != null && currentBoss.hp > 0);

        }, rollDuration + 180 + 350);
    }

    private void applyAttackFromShake(boolean hit) {
        attacksLeft--;

        if (hit) {
            currentBoss.hp -= damage;
            if (currentBoss.hp < 0) currentBoss.hp = 0;

            // optionally play your boss hurt animation here too
            requireActivity().runOnUiThread(() ->
                    playBossOnceThen(
                            R.drawable.anim_hit_once,
                            (currentBoss.hp == 0) ? R.drawable.anim_boss_defeat : R.drawable.anim_boss_idle,
                            720
                    )
            );
        } else {
            // miss: you can show a miss effect, sound, etc.
        }

        requireActivity().runOnUiThread(() -> {
            updateUI();

            if (attacksLeft == 0 || currentBoss.hp == 0) {
                showBattleResult();
            }
        });
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
                    .getCurrentBoss(progress.level - 1);

            activatedEquipment = userEquipmentRepository.getActivatedWithEquipmentByUserId(user.id);
            activateEquipmentWithDuplicates = new ArrayList<>(activatedEquipment);
            sumDuplicateArmor();

            if (currentBoss == null) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "No more bosses left!", Toast.LENGTH_LONG).show();
                    navigateToTasks();
                });
                return;
            }

            bossOriginalHP = currentBoss.hp;
            successRate = progressManager.getSuccessRate(progress.id, progress.level - 1);
            damage = progress.pp;
            rewardCoins = currentBoss.rewardCoins;

            boost();
            barsMax = Math.max(bossOriginalHP, damage);
            requireActivity().runOnUiThread(this::updateUI);
        });
    }

    private void updateUI() {
        if (progress == null || currentBoss == null) return;

        tvBossHpLabel.setText("Boss HP: " + currentBoss.hp + " / " + bossOriginalHP);
        tvPlayerPpLabel.setText("Your PP: " + damage);
        tvAttackChance.setText("Attack chance: " + String.format(Locale.getDefault(), "%.0f", successRate) + "%");
        tvAttacksLeft.setText("Attacks left: " + attacksLeft);
        btnAttack.setEnabled(attacksLeft > 0);

        pbBossHp.setMax(barsMax);
        pbPlayerPp.setMax(barsMax);
        pbPlayerPp.setProgress(damage);
        pbBossHp.setProgress(Math.max(currentBoss.hp, 0));
    }

    private void startBossAnim(int animRes) {
        if (ivBoss == null) return;

        if (bossAnim != null) bossAnim.stop();

        ivBoss.setImageResource(animRes);
        ivBoss.post(() -> {
            bossAnim = (AnimationDrawable) ivBoss.getDrawable();
            if (bossAnim != null) bossAnim.start();
        });
    }

    private void playBossOnceThen(int onceAnimRes, int thenAnimRes, long onceDurationMs) {
        if (bossDefeated) return; // once defeated, never go back

        startBossAnim(onceAnimRes);

        ivBoss.removeCallbacks(resetBossRunnable);
        resetBossRunnable = () -> startBossAnim(thenAnimRes);
        ivBoss.postDelayed(resetBossRunnable, onceDurationMs);
    }

    private Runnable resetBossRunnable;

    private void playHitOnce() {
        ivHit.setVisibility(View.VISIBLE);

        hitAnim = (AnimationDrawable) ivHit.getDrawable();
        if (hitAnim == null) return;

        hitAnim.stop();
        hitAnim.start();

        ivHit.postDelayed(() -> {
            hitAnim.stop();
            ivHit.setVisibility(View.GONE);
        }, 420);
    }


    private void performAttack() {
        if (attacksLeft <= 0 || progress == null || currentBoss == null) return;

        attacksLeft--;
        boolean hit = Math.random() * 100 < successRate;

        if (hit && currentBoss.hp > 0) {
            requireActivity().runOnUiThread(() ->
                    playBossOnceThen(
                            R.drawable.anim_hit_once,
                            R.drawable.anim_boss_idle,
                            720
                    )
            );
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
            bossDefeated = true;
            requireActivity().runOnUiThread(() -> {
                if (resetBossRunnable != null) ivBoss.removeCallbacks(resetBossRunnable);
                startBossAnim(R.drawable.anim_boss_defeat);
            });
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
        for (var e : activateEquipmentWithDuplicates) {
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

    private void sumDuplicateArmor() {
        Map<String, UserEquipment> summedUpEquipment = new HashMap<>();

        for (var e : activatedEquipment) {
            if (!summedUpEquipment.containsKey(e.equipment.getId())) {
                summedUpEquipment.put(e.equipment.getId(), e);
                continue;
            }

            var existing = summedUpEquipment.get(e.equipment.getId());
            if (existing == null) {
                continue;
            }

            existing.equipment.addBonus(e.equipment.getBonus());
        }

        activatedEquipment.clear();
        activatedEquipment.addAll(summedUpEquipment.values());
    }
}