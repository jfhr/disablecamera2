package de.jfhr.disablecamera2;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

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
        updateButtons();
    }

    private void requestAdminPermissionsIfNecessary() {
        if (!policyManager.isAdminActive(componentName)) {
            Intent activateDeviceAdmin = new Intent(ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(activateDeviceAdmin, DPM_ACTIVATION_REQUEST_CODE);
        }
    }

    public void onToggleButtonClick(View view) {
        boolean cameraDisabled = policyManager.getCameraDisabled(componentName);
        writeLog(String.format("Set camera disabled to %b.", !cameraDisabled));
        policyManager.setCameraDisabled(componentName, !cameraDisabled);
        updateButtons();
    }

    private void updateButtons() {
        if (policyManager.getCameraDisabled(componentName)) {
            findToggleButton().setText(R.string.enable_camera);
            findStreamButton().setEnabled(false);
        } else {
            findToggleButton().setText(R.string.disable_camera);
            findStreamButton().setEnabled(true);
        }
    }

    private Button findToggleButton() { return (Button) findViewById(R.id.toggleButton); }

    private Button findStreamButton() { return (Button) findViewById(R.id.streamButton); }

    private TextView findLogView() { return (TextView) findViewById(R.id.logView); }

    private void showError(String what) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(what);
        dlgAlert.setTitle("disableCamera2 error");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the dialog
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void writeLog(String what) {
        findLogView().append(what + "\n");
    }

    public void onStreamButtonClick(View view) {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        if (policyManager.getCameraDisabled(componentName)) {
            writeLog("ERROR: Camera is disabled.");
        }
        else if (!ffmpeg.isSupported()) {
            writeLog("ERROR: FFMPEG is not supported.");
        }

        String[] cmd = {
                "-f",
                "dshow",
                "-i",
                "video=\"Integrated Camera\"",
                "-vcodec",
                "libx264",
                "-f",
                "mpegts",
                "udp://192.168.0.3:1999"
        };
        ffmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                writeLog(message);
            }
            @Override
            public void onProgress(String message) {
                writeLog(message);
            }
            @Override
            public void onFailure(String message) {
                writeLog(message);
            }
            @Override
            public void onStart() {
                findStreamButton().setEnabled(false);
            }
            @Override
            public void onFinish() {
                findStreamButton().setEnabled(true);
            }
        });
    }
}
