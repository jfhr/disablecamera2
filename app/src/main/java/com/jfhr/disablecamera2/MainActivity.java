package com.jfhr.disablecamera2;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.app.admin.DevicePolicyManager.*;

public class MainActivity extends AppCompatActivity {

    private static final int DPM_ACTIVATION_REQUEST_CODE = 100;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        componentName = new ComponentName(this, CameraPolicyReceiver.class);
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        requestAdminPermissionsIfNecessary();
        updateButtonText();
    }

    private void requestAdminPermissionsIfNecessary() {
        if (!policyManager.isAdminActive(componentName)) {
            Intent activateDeviceAdmin = new Intent(ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(activateDeviceAdmin, DPM_ACTIVATION_REQUEST_CODE);
        }
    }

    public void onButtonClick(View view) {
        boolean cameraDisabled = policyManager.getCameraDisabled(componentName);
        policyManager.setCameraDisabled(componentName, !cameraDisabled);
        updateButtonText();
    }

    private Button findButton() {
        return (Button) findViewById(R.id.button);
    }

    private void updateButtonText() {
        if (policyManager.getCameraDisabled(componentName)) {
            findButton().setText(R.string.enable_camera);
        } else {
            findButton().setText(R.string.disable_camera);
        }
    }
}
