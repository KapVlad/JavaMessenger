package com.kap_vlad.android.javachat;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ShowHelper{
    public static void show (Context context, String error) {
        Toast toast = Toast.makeText(context,
                error, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
