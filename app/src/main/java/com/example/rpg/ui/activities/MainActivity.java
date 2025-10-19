package com.example.rpg.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.rpg.R;
import com.example.rpg.databinding.ActivityMainBinding;
import com.example.rpg.prefs.AuthPrefs;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;

    private NavController navController;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        var toolbar = binding.toolbar;

        setSupportActionBar(toolbar);
        var actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }

        drawer = binding.drawerLayout;

        var actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = binding.navigationView;

        navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupWithNavController(navigationView, navController);

        setNavigationListeners();
        setInitNavVisibility();
    }

    public void setAuth(boolean isAuthenticated) {
        setAuthNavVisibility(isAuthenticated);
    }

    private void setNavigationListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                logout();
                drawer.closeDrawers();
                return true;
            }

            var handled = NavigationUI.onNavDestinationSelected(item, navController);
            drawer.closeDrawers();
            return handled;
        });
    }

    private void logout() {
        AuthPrefs.clear(this);
        setAuthNavVisibility(false);

        var opts = new NavOptions.Builder()
                .setPopUpTo(R.id.base_navigation, true)
                .build();

        navController.navigate(R.id.nav_login, null, opts);
    }

    private void setInitNavVisibility() {
        var username = AuthPrefs.getIsAuthenticated(this);
        var isAuthenticated = username != null && !username.isEmpty();
        setAuthNavVisibility(isAuthenticated);
    }

    private void setAuthNavVisibility(boolean isAuthenticated) {
        var menu = navigationView.getMenu();
        menu.setGroupVisible(R.id.auth_group, true);

        var logoutMenuItem = menu.findItem(R.id.nav_logout);
        if (logoutMenuItem != null) {
            logoutMenuItem.setVisible(true);
        }
    }
}