package wesa.xelar.realgoldencoder.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Dp_to_Px {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
