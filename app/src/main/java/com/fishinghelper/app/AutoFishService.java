package com.fishinghelper.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import java.util.Random;

public class AutoFishService extends AccessibilityService {
    
    private static AutoFishService instance;
    private Handler handler;
    private boolean isRunning = false;
    private int currentMode = 1;
    private Random random = new Random();
    
    // Settings
    private SharedPreferences prefs;
    private int reelManualX, reelManualY;
    private int reelAutoX, reelAutoY;
    private int rodJerkX, rodJerkY;
    private float holdDuration, pauseDuration, jerkInterval;
    private int randomRange;
    private float timingVariance;
    
    // State tracking
    private boolean reelAutoActivated = false;
    private int loopCount = 0;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler();
        prefs = getSharedPreferences("FishingHelper", MODE_PRIVATE);
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used for this app
    }
    
    @Override
    public void onInterrupt() {
        stopAutomation();
    }
    
    public static void startAutomation(Context context, int mode) {
        if (instance != null) {
            instance.currentMode = mode;
            instance.loadSettings();
            instance.isRunning = true;
            instance.reelAutoActivated = false;
            instance.loopCount = 0;
            instance.startFishingLoop();
        }
    }
    
    public static void stopAutomation() {
        if (instance != null) {
            instance.isRunning = false;
            instance.handler.removeCallbacksAndMessages(null);
        }
    }
    
    private void loadSettings() {
        reelManualX = prefs.getInt("reelManualX", -1);
        reelManualY = prefs.getInt("reelManualY", -1);
        reelAutoX = prefs.getInt("reelAutoX", -1);
        reelAutoY = prefs.getInt("reelAutoY", -1);
        rodJerkX = prefs.getInt("rodJerkX", -1);
        rodJerkY = prefs.getInt("rodJerkY", -1);
        holdDuration = prefs.getFloat("holdDuration", 2.5f);
        pauseDuration = prefs.getFloat("pauseDuration", 1.0f);
        jerkInterval = prefs.getFloat("jerkInterval", 1.5f);
        randomRange = prefs.getInt("randomRange", 10);
        timingVariance = prefs.getFloat("timingVariance", 0.15f);
    }
    
    private void startFishingLoop() {
        if (!isRunning) return;
        
        loopCount++;
        
        switch (currentMode) {
            case 1:
                executeMode1();
                break;
            case 2:
                executeMode2A();
                break;
            case 3:
                executeMode2B();
                break;
        }
    }
    
    // MODE 1: Stop & Go (Hold reel → Release → Repeat)
    private void executeMode1() {
        humanDelay(() -> {
            // Tap and hold reel manual
            int x = getRandomX(reelManualX);
            int y = getRandomY(reelManualY);
            long duration = getRandomDuration((long)(holdDuration * 1000));
            
            performTapAndHold(x, y, duration, () -> {
                // After release, pause
                long pause = getRandomDuration((long)(pauseDuration * 1000));
                handler.postDelayed(() -> {
                    startFishingLoop(); // Repeat
                }, pause);
            });
        });
    }
    
    // MODE 2A: Auto Reel + Jerk (Tap auto once, then jerk repeatedly)
    private void executeMode2A() {
        if (!reelAutoActivated) {
            // First loop: activate auto reel
            humanDelay(() -> {
                int x = getRandomX(reelAutoX);
                int y = getRandomY(reelAutoY);
                performTap(x, y, () -> {
                    reelAutoActivated = true;
                    // Wait then start jerking
                    handler.postDelayed(() -> startFishingLoop(), 500);
                });
            });
        } else {
            // Jerk rod
            humanDelay(() -> {
                int x = getRandomX(rodJerkX);
                int y = getRandomY(rodJerkY);
                performTap(x, y, () -> {
                    // Wait jerk interval then repeat
                    long interval = getRandomDuration((long)(jerkInterval * 1000));
                    handler.postDelayed(() -> startFishingLoop(), interval);
                });
            });
        }
    }
    
    // MODE 2B: Manual Reel + Jerk (Hold reel → Release → Jerk → Repeat)
    private void executeMode2B() {
        humanDelay(() -> {
            // Tap and hold reel manual
            int x1 = getRandomX(reelManualX);
            int y1 = getRandomY(reelManualY);
            long duration = getRandomDuration((long)(holdDuration * 1000));
            
            performTapAndHold(x1, y1, duration, () -> {
                // After release, short pause then jerk
                handler.postDelayed(() -> {
                    int x2 = getRandomX(rodJerkX);
                    int y2 = getRandomY(rodJerkY);
                    performTap(x2, y2, () -> {
                        // Pause then repeat
                        long pause = getRandomDuration((long)(pauseDuration * 1000));
                        handler.postDelayed(() -> startFishingLoop(), pause);
                    });
                }, 200 + random.nextInt(200));
            });
        });
    }
    
    // Perform simple tap
    private void performTap(int x, int y, Runnable callback) {
        Path path = new Path();
        path.moveTo(x, y);
        
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        
        dispatchGesture(builder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                if (callback != null) {
                    handler.postDelayed(callback, 50 + random.nextInt(100));
                }
            }
        }, null);
    }
    
    // Perform tap and hold
    private void performTapAndHold(int x, int y, long duration, Runnable callback) {
        Path path = new Path();
        path.moveTo(x, y);
        
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        
        dispatchGesture(builder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                if (callback != null) {
                    handler.postDelayed(callback, 50 + random.nextInt(100));
                }
            }
        }, null);
    }
    
    // Randomization helpers
    private int getRandomX(int baseX) {
        return baseX + random.nextInt(randomRange * 2) - randomRange;
    }
    
    private int getRandomY(int baseY) {
        return baseY + random.nextInt(randomRange * 2) - randomRange;
    }
    
    private long getRandomDuration(long baseDuration) {
        float variance = 1.0f + (random.nextFloat() * timingVariance * 2 - timingVariance);
        return (long)(baseDuration * variance);
    }
    
    // Human-like delay before action
    private void humanDelay(Runnable action) {
        handler.postDelayed(action, 50 + random.nextInt(150));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutomation();
        instance = null;
    }
}
