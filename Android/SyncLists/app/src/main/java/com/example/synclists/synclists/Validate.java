package com.example.synclists.synclists;

/**
 * Created by SirChickenHair on 11/29/14.
 */
public final class Validate {
    public static boolean email(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public Validate() throws Exception {
        throw new Exception("Cannot instantiate object of type Validate");
    }
}
