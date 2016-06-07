package com.example.user.myapplication.cu;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Wang on 2015/12/7.
 */
public class Utils {

    public static void showSnackbar(final View view, String content){
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(
                                view.getContext(),
                                "Action",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

}
