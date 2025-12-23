package com.fishinghelper.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private TextView tvStatus;
    private Button btnStop;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tvStatus = findViewById(R.id.tvStatus);
        btnStop = findViewById(R.id.btnStop);
        
        // Check permissions
        if (!hasOverlayPermission()) {
            requestOverlayPermission();
            return;
        }
        
        if (!isAccessibilityEnabled()) {
            requestAccessibilityPermission();
            return;
        }
        
        // Start overlay service
        startOverlayService();
        
        // Setup stop button
        btnStop.setOnClickListener(v -> stopApp());
        
        updateStatus();
    }
    
    private void startOverlayService() {
        Intent intent = new Intent(this, OverlayService.class);
        startService(intent);
        Toast.makeText(this, "Overlay started! Tap floating icon in game", Toast.LENGTH_LONG).show();
    }
    
    private void stopApp() {
        // Stop overlay service
        Intent intent = new Intent(this, OverlayService.class);
        stopService(intent);
        
        // Stop automation if running
        AutoFishService.stopAutomation();
        
        Toast.makeText(this, "Fishing Helper stopped", Toast.LENGTH_SHORT).show();
        
        // Close app after short delay
        new Handler().postDelayed(() -> {
            finish();
            System.exit(0);
        }, 500);
    }
    
    private void updateStatus() {
        tvStatus.setText("🟢 Overlay Active\n\nYou can minimize this app.\nOverlay will stay visible in game.\n\nTap floating icon to access menu.");
    }
    
    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }
    
    private void requestOverlayPermission() {
        Toast.makeText(this, "Please enable Overlay permission", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(intent);
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
    
    private void requestAccessibilityPermission() {
        Toast.makeText(this, "Please enable Accessibility service", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Check permissions again when returning from settings
        if (hasOverlayPermission() && isAccessibilityEnabled()) {
            startOverlayService();
            updateStatus();
        }
    }
}