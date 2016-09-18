package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Util {
private static final int DEFAULT_DENSITY = 1024;
    
    public static Bitmap getScaledBitmapAlpha8(Context context, int id) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.ALPHA_8;
        bitmapOptions.inScaled = true;
        bitmapOptions.inDensity = DEFAULT_DENSITY;
        bitmapOptions.inTargetDensity = (int)(getScaleFactor(context)*DEFAULT_DENSITY);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), id, bitmapOptions);
        b.setDensity(context.getResources().getDisplayMetrics().densityDpi);
        return b;
    }
    
    public static Bitmap getDownScaledBitmapAlpha8(Context context, int id) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.ALPHA_8;
        bitmapOptions.inScaled = true;
        bitmapOptions.inDensity = DEFAULT_DENSITY;
        bitmapOptions.inTargetDensity = Math.min((int)(getScaleFactor(context)*DEFAULT_DENSITY), DEFAULT_DENSITY);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), id, bitmapOptions);
        b.setDensity(context.getResources().getDisplayMetrics().densityDpi);
        return b;
    }
    
    public static Bitmap getBitmapAlpha8(Context context, int id) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.ALPHA_8;
        return BitmapFactory.decodeResource(context.getResources(), id, bitmapOptions);
    }
    
    /**
     * Returns a scale factor related to the screen resolution that is used for scaling bitmaps.
     * @param context
     * @return
     */
    public static float getScaleFactor(Context context){
        // 1.2 @ 720x1280 px
        return context.getResources().getDisplayMetrics().heightPixels / 1066f;
    }
}
