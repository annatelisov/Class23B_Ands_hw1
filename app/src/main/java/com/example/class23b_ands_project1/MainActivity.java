package com.example.class23b_ands_project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {

    private MaterialButton main_BTN_continue;
    private EditText main_TXT_box;
    private int battery;
    private int curBrightnessValue;
    private Boolean isFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_BTN_continue = findViewById(R.id.main_BTN_continue);
        main_TXT_box = findViewById(R.id.main_TXT_box);

        battery = getBatteryPercentage(this);
        curBrightnessValue = brightnessValue(curBrightnessValue);

        isFlash();

        main_BTN_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curBrightnessValue = brightnessValue(curBrightnessValue);
                isFlash();
                if (!(main_TXT_box.getText().toString().isEmpty())) {
                    if ((battery > 30) && (curBrightnessValue > 1000) && (getDeviceType().equals("Xiaomi")) && (isFlash)) {
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry, some of your parameters not good", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "please enter password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Battery Check
    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }

    //Brightness check
    public int brightnessValue(int curBrightnessValue){
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
        return curBrightnessValue;
    }

    //Device type check
    public String getDeviceType() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer;
    }

    //If the flash is on\off check
    private void isFlash() {
        CameraManager CM = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String CID = CM.getCameraIdList()[0];

            CM.registerTorchCallback(new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeChanged(String CID, boolean enabled) {
                    super.onTorchModeChanged(CID, enabled);
                    if (!enabled) {
                        isFlash = false;
                    } else {
                        isFlash = true;
                    }
                }
            }, new Handler());

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}