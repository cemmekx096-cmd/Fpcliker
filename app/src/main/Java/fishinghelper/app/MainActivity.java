package com.fishinghelper.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private SharedPreferences prefs;
    
    // UI Elements
    private TextView tvReelManual, tvReelAuto, tvRodJerk;
    private Button btnSetupCoords, btnStart, btnStop;
    private RadioGroup rgMode;
    private SeekBar sbHoldDuration, sbPauseDuration, sbJerkInterval;
    private SeekBar sbRandomRange, sbTimingVariance;
    private TextView tvHold, tvPause, tvJerk, tvRandom, tvVariance;
    
    // Coordinates
    private int reelManualX = -1, reelManualY = -1;
    private int reelAutoX = -1, reelAutoY = -1;
    private int rodJerkX = -1, rodJerkY = -1;
    
    // Settings
    private int selectedMode = 1; // 1=Stop&Go, 2=AutoReel+Jerk, 3=ManualReel+Jerk
    private float holdDuration = 2.5f;
    private float pauseDuration = 1.0f;
    private float jerkInterval = 1.5f;
    private int randomRange = 10;
    private float timingVariance = 0.15f;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);
            
            prefs = getSharedPreferences("FishingHelper", MODE_PRIVATE);
            
            initViews();
            loadSettings();
            setupListeners();
            checkAccessibilityPermission();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        try {
            // Coordinate displays
            tvReelManual = findViewById(R.id.tvReelManual);
            tvReelAuto = findViewById(R.id.tvReelAuto);
            tvRodJerk = findViewById(R.id.tvRodJerk);
            
            // Buttons
            btnSetupCoords = findViewById(R.id.btnSetupCoords);
            btnStart = findViewById(R.id.btnStart);
            btnStop = findViewById(R.id.btnStop);
            
            // Mode selection
            rgMode = findViewById(R.id.rgMode);
            
            // Timing sliders
            sbHoldDuration = findViewById(R.id.sbHoldDuration);
            sbPauseDuration = findViewById(R.id.sbPauseDuration);
            sbJerkInterval = findViewById(R.id.sbJerkInterval);
            tvHold = findViewById(R.id.tvHold);
            tvPause = findViewById(R.id.tvPause);
            tvJerk = findViewById(R.id.tvJerk);
            
            // Anti-detection sliders
            sbRandomRange = findViewById(R.id.sbRandomRange);
            sbTimingVariance = findViewById(R.id.sbTimingVariance);
            tvRandom = findViewById(R.id.tvRandom);
            tvVariance = findViewById(R.id.tvVariance);
            
            if (btnStop != null) btnStop.setEnabled(false);
            
            // Check for null views
            if (tvReelManual == null || btnSetupCoords == null || btnStart == null) {
                throw new RuntimeException("Layout inflation failed - views are null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to initialize views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void loadSettings() {
        // Load coordinates
        reelManualX = prefs.getInt("reelManualX", -1);
        reelManualY = prefs.getInt("reelManualY", -1);
        reelAutoX = prefs.getInt("reelAutoX", -1);
        reelAutoY = prefs.getInt("reelAutoY", -1);
        rodJerkX = prefs.getInt("rodJerkX", -1);
        rodJerkY = prefs.getInt("rodJerkY", -1);
        
        updateCoordinateDisplays();
        
        // Load settings
        holdDuration = prefs.getFloat("holdDuration", 2.5f);
        pauseDuration = prefs.getFloat("pauseDuration", 1.0f);
        jerkInterval = prefs.getFloat("jerkInterval", 1.5f);
        randomRange = prefs.getInt("randomRange", 10);
        timingVariance = prefs.getFloat("timingVariance", 0.15f);
        selectedMode = prefs.getInt("selectedMode", 1);
        
        // Set UI values
        sbHoldDuration.setProgress((int)(holdDuration * 10) - 10);
        sbPauseDuration.setProgress((int)(pauseDuration * 10) - 5);
        sbJerkInterval.setProgress((int)(jerkInterval * 10) - 5);
        sbRandomRange.setProgress(randomRange - 5);
        sbTimingVariance.setProgress((int)(timingVariance * 100) - 5);
        
        if (selectedMode == 1) rgMode.check(R.id.rbMode1);
        else if (selectedMode == 2) rgMode.check(R.id.rbMode2A);
        else rgMode.check(R.id.rbMode2B);
        
        updateLabels();
    }
    
    private void setupListeners() {
        btnSetupCoords.setOnClickListener(v -> startCoordinateSetup());
        
        btnStart.setOnClickListener(v -> startAutomation());
        
        btnStop.setOnClickListener(v -> stopAutomation());
        
        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMode1) selectedMode = 1;
            else if (checkedId == R.id.rbMode2A) selectedMode = 2;
            else selectedMode = 3;
            saveSettings();
        });
        
        sbHoldDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                holdDuration = (progress + 10) / 10.0f;
                tvHold.setText(String.format("Hold: %.1fs", holdDuration));
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) { saveSettings(); }
        });
        
        sbPauseDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                pauseDuration = (progress + 5) / 10.0f;
                tvPause.setText(String.format("Pause: %.1fs", pauseDuration));
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) { saveSettings(); }
        });
        
        sbJerkInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                jerkInterval = (progress + 5) / 10.0f;
                tvJerk.setText(String.format("Jerk: %.1fs", jerkInterval));
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) { saveSettings(); }
        });
        
        sbRandomRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                randomRange = progress + 5;
                tvRandom.setText(String.format("Random: ±%dpx", randomRange));
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) { saveSettings(); }
        });
        
        sbTimingVariance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                timingVariance = (progress + 5) / 100.0f;
                tvVariance.setText(String.format("Variance: ±%.0f%%", timingVariance * 100));
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) { saveSettings(); }
        });
    }
    
    private void updateLabels() {
        tvHold.setText(String.format("Hold: %.1fs", holdDuration));
        tvPause.setText(String.format("Pause: %.1fs", pauseDuration));
        tvJerk.setText(String.format("Jerk: %.1fs", jerkInterval));
        tvRandom.setText(String.format("Random: ±%dpx", randomRange));
        tvVariance.setText(String.format("Variance: ±%.0f%%", timingVariance * 100));
    }
    
    private void updateCoordinateDisplays() {
        if (reelManualX != -1) {
            tvReelManual.setText(String.format("✓ Reel Manual: (%d, %d)", reelManualX, reelManualY));
        } else {
            tvReelManual.setText("✗ Reel Manual: Not Set");
        }
        
        if (reelAutoX != -1) {
            tvReelAuto.setText(String.format("✓ Reel Auto: (%d, %d)", reelAutoX, reelAutoY));
        } else {
            tvReelAuto.setText("✗ Reel Auto: Not Set");
        }
        
        if (rodJerkX != -1) {
            tvRodJerk.setText(String.format("✓ Rod Jerk: (%d, %d)", rodJerkX, rodJerkY));
        } else {
            tvRodJerk.setText("✗ Rod Jerk: Not Set");
        }
    }
    
    private void startCoordinateSetup() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please enable overlay permission first", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            return;
        }
        
        Intent intent = new Intent(this, CoordinateSetupActivity.class);
        startActivityForResult(intent, 100);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            reelManualX = data.getIntExtra("reelManualX", -1);
            reelManualY = data.getIntExtra("reelManualY", -1);
            reelAutoX = data.getIntExtra("reelAutoX", -1);
            reelAutoY = data.getIntExtra("reelAutoY", -1);
            rodJerkX = data.getIntExtra("rodJerkX", -1);
            rodJerkY = data.getIntExtra("rodJerkY", -1);
            
            saveCoordinates();
            updateCoordinateDisplays();
            Toast.makeText(this, "Coordinates saved!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveCoordinates() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("reelManualX", reelManualX);
        editor.putInt("reelManualY", reelManualY);
        editor.putInt("reelAutoX", reelAutoX);
        editor.putInt("reelAutoY", reelAutoY);
        editor.putInt("rodJerkX", rodJerkX);
        editor.putInt("rodJerkY", rodJerkY);
        editor.apply();
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("holdDuration", holdDuration);
        editor.putFloat("pauseDuration", pauseDuration);
        editor.putFloat("jerkInterval", jerkInterval);
        editor.putInt("randomRange", randomRange);
        editor.putFloat("timingVariance", timingVariance);
        editor.putInt("selectedMode", selectedMode);
        editor.apply();
    }
    
    private void startAutomation() {
        // Validate coordinates
        if (selectedMode == 1 && reelManualX == -1) {
            Toast.makeText(this, "Please setup Reel Manual coordinate", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMode == 2 && (reelAutoX == -1 || rodJerkX == -1)) {
            Toast.makeText(this, "Please setup Reel Auto and Rod Jerk coordinates", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMode == 3 && (reelManualX == -1 || rodJerkX == -1)) {
            Toast.makeText(this, "Please setup Reel Manual and Rod Jerk coordinates", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isAccessibilityEnabled()) {
            Toast.makeText(this, "Please enable Accessibility Service", Toast.LENGTH_LONG).show();
            checkAccessibilityPermission();
            return;
        }
        
        // Start service
        AutoFishService.startAutomation(this, selectedMode);
        
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        Toast.makeText(this, "Automation started!", Toast.LENGTH_SHORT).show();
    }
    
    private void stopAutomation() {
        AutoFishService.stopAutomation();
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        Toast.makeText(this, "Automation stopped!", Toast.LENGTH_SHORT).show();
    }
    
    private void checkAccessibilityPermission() {
        if (!isAccessibilityEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable Fishing Helper accessibility service", Toast.LENGTH_LONG).show();
        }
    }
    
    private boolean isAccessibilityEnabled() {
        try {
            int enabled = Settings.Secure.getInt(getContentResolver(), 
                Settings.Secure.ACCESSIBILITY_ENABLED);
            if (enabled == 1) {
                String services = Settings.Secure.getString(getContentResolver(), 
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                return services != null && services.contains(getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
            }
