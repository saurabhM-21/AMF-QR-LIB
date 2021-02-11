package com.americana.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PermissionUtils {


    public static boolean checkCameraPermissions(Activity context) {
        int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }


}