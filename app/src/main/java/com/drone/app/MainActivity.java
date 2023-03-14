package com.drone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.drone.app.fragments.ControllerFragment;
import com.drone.app.fragments.DroneConditionFragment;
import com.drone.app.fragments.DroneSettingFragment;
import com.drone.app.fragments.ProfileFragment;
import com.drone.app.serial.DevicesFragment;
import com.drone.app.serial.TerminalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initBottomNavigation();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout, new DevicesFragment(), "devices")
                    .commit();
        } else {
            onBackStackChanged();
        }
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initBottomNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        navView.setOnNavigationItemSelectedListener(this);
        LoadFragment(new ControllerFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.nav_controller) {
            LoadFragment(new ControllerFragment());
        } else if (item.getItemId() == R.id.nav_condition) {
            LoadFragment(new DroneConditionFragment());
        } else if (item.getItemId() == R.id.nav_setting) {
            LoadFragment(new DroneSettingFragment());
        } else if (item.getItemId() == R.id.nav_profile) {
            LoadFragment(new ProfileFragment());
        } else if (item.getItemId() == R.id.nav_devices) {
            LoadFragment(new DevicesFragment());
        }
        return true;
    }

    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
            TerminalFragment terminal = (TerminalFragment) getSupportFragmentManager().findFragmentByTag("terminal");
            if (terminal != null) terminal.status("USB device detected");
        }
        super.onNewIntent(intent);
    }
}