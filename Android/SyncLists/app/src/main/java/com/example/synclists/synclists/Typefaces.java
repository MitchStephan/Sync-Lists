package com.example.synclists.synclists;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by SirChickenHair on 11/26/14.
 */
public class Typefaces {
    private static Typeface typeface;

    public static Typeface get(Context c) {

        if(typeface == null) {
            typeface = Typeface.createFromAsset(c.getAssets(),
                    Constants.FONTAWESOME_ASSETS);
        }

        return typeface;
    }
}
