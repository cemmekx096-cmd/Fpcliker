package com.fishinghelper.app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {
    
    private WindowManager windowManager;
    private FrameLayout floatingIcon;
    private LinearLayout menuPopup;
    private SharedPreferences prefs;
    
    // State
    private boolean isMenuOpen = false;
    private int selectedMode = 1;
    
    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        prefs = getSharedPreferences("FishingHelper", MODE_PRIVATE);
        
        createFloatingIcon();
    }
    
    private void createFloatingIcon() {
        // Create floating icon
        floatingIcon = new FrameLayout(this);
        
        // Icon view (simple colored circle dengan text)
        TextView iconView = new TextView(this);
        iconView.setText("🎣");
        iconView.setTextSize(32);
        iconView.setBackgroundColor(Color.parseColor("#4CAF50"));
        iconView.setTextColor(Color.WHITE);
        iconView.setGravity(Gravity.CENTER);
        iconView.setPadding(10, 10, 10, 10);
        
        floatingIcon.addView(iconView);
        
        // Layout params
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            120, 120,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 50;
        params.y = 500;
        
        // Add to window
        windowManager.addView(floatingIcon, params);
        
        // Make draggable
        makeDraggable(floatingIcon, params);
        
        // Click to open menu
        floatingIcon.setOnClickListener(v -> toggleMenu());
    }
    
    private void makeDraggable(View view, WindowManager.LayoutParams params) {
        final int[] lastAction = new int[1];
        final float[] initialX = new float[1];
        final float[] initialY = new float[1];
        final float[] initialTouchX = new float[1];
        final float[] initialTouchY = new float[1];
        
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX[0] = params.x;
                    initialY[0] = params.y;
                    initialTouchX[0] = event.getRawX();
                    initialTouchY[0] = event.getRawY();
                    lastAction[0] = event.getAction();
                    return true;
                    
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX[0] + (int)(initialTouchX[0] - event.getRawX());
                    params.y = initialY[0] + (int)(event.getRawY() - initialTouchY[0]);
                    windowManager.updateViewLayout(floatingIcon, params);
                    lastAction[0] = event.getAction();
                    return true;
                    
                case MotionEvent.ACTION_UP:
                    if (lastAction[0] == MotionEvent.ACTION_DOWN) {
                        // It was a click, not drag
                        v.performClick();
                    }
                    return true;
            }
            return false;
        });
    }
    
    private void toggleMenu() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }
    
    private void openMenu() {
        if (menuPopup != null) return; // Already open
        
        // Create menu popup
        menuPopup = new LinearLayout(this);
        menuPopup.setOrientation(LinearLayout.VERTICAL);
        menuPopup.setBackgroundColor(Color.parseColor("#DD000000"));
        menuPopup.setPadding(20, 20, 20, 20);
        
        // Header with close button
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView title = new TextView(this);
        title.setText("⚙️ Settings");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        Button closeBtn = new Button(this);
        closeBtn.setText("❌");
        closeBtn.setTextSize(16);
        closeBtn.setOnClickListener(v -> closeMenu());
        
        header.addView(title);
        header.addView(closeBtn);
        menuPopup.addView(header);
        
        // Menu items
        addMenuItem("📍 Setup Coordinates", () -> openCoordinateSetup());
        addMenuItem("🎮 Select Mode", () -> openModeSelection());
        addMenuItem("⏱️ Timing Settings", () -> openTimingSettings());
        addMenuItem("▶️ START Automation", () -> startAutomation());
        addMenuItem("⏹️ STOP Automation", () -> stopAutomation());
        
        // Layout params
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            400, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;
        
        windowManager.addView(menuPopup, params);
        isMenuOpen = true;
    }
    
    private void closeMenu() {
        if (menuPopup != null) {
            windowManager.removeView(menuPopup);
            menuPopup = null;
            isMenuOpen = false;
        }
    }
    
    private void addMenuItem(String text, Runnable action) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#333333"));
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 0);
        btn.setLayoutParams(params);
        
        btn.setOnClickListener(v -> action.run());
        menuPopup.addView(btn);
    }
    
    private void openCoordinateSetup() {
        Toast.makeText(this, "Coordinate setup - Coming in next update", Toast.LENGTH_SHORT).show();
        closeMenu();
        // TODO: Implement draggable crosshair system
    }
    
    private void openModeSelection() {
        Toast.makeText(this, "Mode selection - Coming in next update", Toast.LENGTH_SHORT).show();
        closeMenu();
        // TODO: Implement mode selection dialog
    }
    
    private void openTimingSettings() {
        Toast.makeText(this, "Timing settings - Coming in next update", Toast.LENGTH_SHORT).show();
        closeMenu();
        // TODO: Implement timing sliders
    }
    
    private void startAutomation() {
        selectedMode = prefs.getInt("selectedMode", 1);
        AutoFishService.startAutomation(this, selectedMode);
        Toast.makeText(this, "Automation started!", Toast.LENGTH_SHORT).show();
        closeMenu();
        
        // Change icon color to indicate active
        updateIconColor(Color.parseColor("#2196F3")); // Blue = active
    }
    
    private void stopAutomation() {
        AutoFishService.stopAutomation();
        Toast.makeText(this, "Automation stopped", Toast.LENGTH_SHORT).show();
        closeMenu();
        
        // Change icon color back to green
        updateIconColor(Color.parseColor("#4CAF50"));
    }
    
    private void updateIconColor(int color) {
        if (floatingIcon != null && floatingIcon.getChildCount() > 0) {
            View child = floatingIcon.getChildAt(0);
            child.setBackgroundColor(color);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingIcon != null) {
            windowManager.removeView(floatingIcon);
        }
        if (menuPopup != null) {
            windowManager.removeView(menuPopup);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}