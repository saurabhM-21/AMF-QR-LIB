package com.americana.common;

import android.content.Context;

public class AppCommon {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AppCommon.context = context;
    }
}
