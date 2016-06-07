package com.example.user.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by User on 2015/9/6.
 */
public class ScrollFunActivity extends Activity {

    String TAG = "ScrollFunActivity";

    PullScrollView scroll_mine;
    View iv_header, dHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_fun);

        scroll_mine = (PullScrollView)findViewById(R.id.scroll_mine);
        iv_header = findViewById(R.id.iv_header);
        dHeader = findViewById(R.id.dHeader);

        dHeader.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "RelativeLayout.LayoutParams  mw=" + iv_header.getMeasuredWidth() + ", mh=" + iv_header.getMeasuredHeight());
                Log.i(TAG, "RelativeLayout.LayoutParams  w=" + iv_header.getWidth() + ", h=" + iv_header.getHeight());
                dHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, iv_header.getHeight()));
            }
        });

        scroll_mine.setHeader(findViewById(R.id.view_header), findViewById(R.id.iv_header));
    }

    void besides(){
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
    }

    /**
     * reflect - 反射
     */
    protected void otherCode() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class.forName("com.example.user.myapplication.ScrollFunActivity").newInstance();

        Class<?> clazz = Class.forName("com.example.user.myapplication.ScrollFunActivity");
        Constructor<?> constructor = clazz.getDeclaredConstructor(boolean.class);
        ScrollFunActivity scrollFunActivity = (ScrollFunActivity)constructor.newInstance(false);
        scrollFunActivity.otherFun(111, "111");

        Class<?> clazz2 = Class.forName("com.example.user.myapplication.ScrollFunActivity");
        ScrollFunActivity scrollFunActivity2 = (ScrollFunActivity) clazz2.newInstance();
        Field field = clazz2.getField("otherBoolean");
        field.setAccessible(true);
        field.setBoolean(scrollFunActivity2, true);

        Class<?> clazz3 = Class.forName("com.example.user.myapplication.ScrollFunActivity");
        ScrollFunActivity scrollFunActivity3 = (ScrollFunActivity) clazz3.newInstance();
        Method method = clazz3.getMethod("otherFun", long.class, String.class);
        method.invoke(scrollFunActivity3, 333, "333");

        ScrollFunActivity scrollFunActivity4 = new ScrollFunActivity();
        Proxy.newProxyInstance(ArcView.class.getClassLoader(),
                                new Class[]{ArcView.class},
                                new MInvocationHandle(scrollFunActivity4)); // 动态代理
    }

    class MInvocationHandle implements InvocationHandler {

        ScrollFunActivity scrollFunActivity;

        public MInvocationHandle(ScrollFunActivity scrollFunActivity){
            this.scrollFunActivity = scrollFunActivity;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Object ret = null;
            long start = System.currentTimeMillis();
            if(methodName.equals("otherFun")) {
                ret = method.invoke(scrollFunActivity, args);
            } else {
                if(scrollFunActivity.otherBoolean) {
                    ret = method.invoke(scrollFunActivity, args);
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("[方法调用][" + methodName + "]耗时:" + (end - start) + "ms");
            return ret;
        }
    }

    protected boolean otherBoolean = false;

    protected void otherFun(long l, String s){
        if (otherBoolean) System.out.println("otherBoolean");
    }

    private void otherCode2(){
        HandlerThread handlerThread = new HandlerThread("fun");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 没有在主线程了
            }
        });
    }
}
