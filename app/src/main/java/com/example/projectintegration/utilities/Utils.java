package com.example.projectintegration.utilities;

import android.app.Activity;
import android.os.Build;
import androidx.core.content.ContextCompat;

public class Utils {

    public static void changeStatusBarColor(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(
                    ContextCompat.getColor(activity, colorResId)
            );
        }
    }
}
