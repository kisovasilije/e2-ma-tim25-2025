package com.example.rpg.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.rpg.R;
import com.example.rpg.database.AppDatabase;
import com.example.rpg.model.Player;
import com.example.rpg.ui.fragments.BattleFragment;
import com.example.rpg.ui.fragments.CategoryFragment;
import com.example.rpg.ui.fragments.RegistrationFragment;
import com.example.rpg.ui.fragments.TaskFragment;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize or reset player at app start
        Executors.newSingleThreadExecutor().execute(() -> {
            Player player = AppDatabase.get(this).playerDao().getCurrentPlayer();

            if (player == null) {
                // First-time player
                player = new Player("Hero", 1, 100, 100);
                AppDatabase.get(this).playerDao().insertPlayer(player);
            } else {
                // Reset once at app start
                player.level = 1;
                player.pp = 100;
                player.coins = 100;
                AppDatabase.get(this).playerDao().updatePlayer(player);
            }
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }

        // Load default fragment
        navigateToFragment(new RegistrationFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_signup) {
            navigateToFragment(new RegistrationFragment());
            return true;
        } else if (id == R.id.nav_category) {
            navigateToFragment(new CategoryFragment());
            return true;
        } else if (id == R.id.nav_tasks) {
            navigateToFragment(new TaskFragment());
            return true;
        } else if (id == R.id.nav_battle) {
            navigateToFragment(new BattleFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
