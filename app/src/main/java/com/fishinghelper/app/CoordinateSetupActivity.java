package com.fishinghelper.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinateSetupActivity extends Activity {
    
    private WindowManager windowManager;
    private OverlayView overlayView;
    private TextView tvInstruction;
    private Button btnDone, btnCancel;
    
    private int currentStep = 0; // 0=reelManual, 1=reelAuto, 2=rodJerk
    private int reelManualX = -1, reelManualY = -1;
    private int reelAutoX = -1, reelAutoY = -1;
    private int rodJerkX = -1, rodJerkY = -1;
    
    private String[] instructions = {
        "Step 1/3: Tap REEL MANUAL button (Yellow circle, left)",
        "Step 2/3: Tap REEL AUTO button (Green circle, left bottom)",
        "Step 3/3: Tap ROD JERK button (Blue circle, right)"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate_setup);
        
        tvInstruction = findViewById(R.id.tvInstruction);
        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);
        
        btnDone.setEnabled(false);
        
        tvInstruction.setText(instructions[0]);
        
        btnDone.setOnClickListener(v -> finishSetup());
        btnCancel.setOnClickListener(v -> {
            removeOverlay();
            finish();
        });
        
        setupOverlay();
        
        // Auto-minimize setelah 2 detik
        tvInstruction.postDelayed(() -> {
            moveTaskToBack(true);
            Toast.makeText(this, "Buka game Fishing Planet untuk setup koordinat", Toast.LENGTH_LONG).show();
        }, 2000);
    }
    
    private void setupOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        );
        
        overlayView = new OverlayView(this);
        windowManager.addView(overlayView, params);
        
        overlayView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                handleCoordinateTap(x, y);
                return true;
            }
            return false;
        });
    }
    
    private void handleCoordinateTap(int x, int y) {
        if (currentStep > 2) return; // Prevent extra taps after completion
        
        switch (currentStep) {
            case 0:
                reelManualX = x;
                reelManualY = y;
                overlayView.addMarker(x, y, "Reel Manual", Color.YELLOW);
                currentStep++;
                tvInstruction.setText(instructions[1]);
                Toast.makeText(this, "✓ Reel Manual set! Tap Reel Auto next", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                reelAutoX = x;
                reelAutoY = y;
                overlayView.addMarker(x, y, "Reel Auto", Color.GREEN);
                currentStep++;
                tvInstruction.setText(instructions[2]);
                Toast.makeText(this, "✓ Reel Auto set! Tap Rod Jerk next", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                rodJerkX = x;
                rodJerkY = y;
                overlayView.addMarker(x, y, "Rod Jerk", Color.CYAN);
                currentStep++;
                btnDone.setEnabled(true);
                tvInstruction.setText("All coordinates set! Press DONE");
                Toast.makeText(this, "✓ All set! Press DONE button", Toast.LENGTH_LONG).show();
                
                // Disable overlay setelah semua setup
                overlayView.setOnTouchListener(null);
                overlayView.setClickable(false);
                break;
        }
    }
    
    private void finishSetup() {
        Intent result = new Intent();
        result.putExtra("reelManualX", reelManualX);
        result.putExtra("reelManualY", reelManualY);
        result.putExtra("reelAutoX", reelAutoX);
        result.putExtra("reelAutoY", reelAutoY);
        result.putExtra("rodJerkX", rodJerkX);
        result.putExtra("rodJerkY", rodJerkY);
        setResult(RESULT_OK, result);
        removeOverlay();
        finish();
    }
    
    private void removeOverlay() {
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeOverlay();
    }
    
    // Custom view to draw markers
    private class OverlayView extends View {
        private Paint paint;
        private Paint textPaint;
        private java.util.ArrayList<Marker> markers;
        
        class Marker {
            int x, y, color;
            String label;
            Marker(int x, int y, String label, int color) {
                this.x = x;
                this.y = y;
                this.label = label;
                this.color = color;
            }
        }
        
        public OverlayView(Activity context) {
            super(context);
            markers = new java.util.ArrayList<>();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(40);
            textPaint.setAntiAlias(true);
            textPaint.setShadowLayer(5, 0, 0, Color.BLACK);
        }
        
        public void addMarker(int x, int y, String label, int color) {
            markers.add(new Marker(x, y, label, color));
            invalidate();
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            for (Marker m : markers) {
                paint.setColor(m.color);
                // Draw crosshair
                canvas.drawLine(m.x - 50, m.y, m.x + 50, m.y, paint);
                canvas.drawLine(m.x, m.y - 50, m.x, m.y + 50, paint);
                canvas.drawCircle(m.x, m.y, 40, paint);
                
                // Draw label
                canvas.drawText(m.label, m.x - 80, m.y - 60, textPaint);
            }
        }
    }
}