package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

public class GetScreenOrientation {


    public static boolean GetListByScreenSize(Context context) {

        boolean isRotated=false;
        assert context.getSystemService(Context.WINDOW_SERVICE) != null;
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                break;
            case Surface.ROTATION_90:
                isRotated=true;
                break;
            case Surface.ROTATION_180:
                isRotated=true;
                break;
            case Surface.ROTATION_270:
                break;
        }

        return isRotated;
    }
}
