package com.jfhr.disablecamera2;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class CameraPolicyReceiver extends DeviceAdminReceiver {
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }
}