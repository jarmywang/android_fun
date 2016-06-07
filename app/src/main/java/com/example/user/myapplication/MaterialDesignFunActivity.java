package com.example.user.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewTreeObserver;

/**
 * Created by User on 2015/10/20.
 */
public class MaterialDesignFunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_material_design_fun);
            final FloatingActionMButton floatingActionButton = (FloatingActionMButton)findViewById(R.id.fab);
            floatingActionButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() { // 获取控件宽高最靠谱例子
                    floatingActionButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    System.out.println("floatingActionButton.W="+floatingActionButton.getWidth() + "floatingActionButton.H="+floatingActionButton.getHeight());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
