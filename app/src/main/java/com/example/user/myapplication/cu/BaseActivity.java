package com.example.user.myapplication.cu;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Wang on 2015/12/28.
 */
public class BaseActivity extends AppCompatActivity {

    public int getViewRes() {
        try {
            Class<?> clazz = this.getClass();
            ContentView contentView = clazz.getAnnotation(ContentView.class);
            if (contentView != null) {
                return contentView.value();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void loadContentView() {
        try {
            Class<?> clazz = this.getClass();
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation : annotations) {
                Log.v("BaseActivity", "annotation="+annotation.toString());
            }
            ContentView contentView = clazz.getAnnotation(ContentView.class);
            if (contentView != null) {
                setContentView(contentView.value());
//                Method setContentViewMethod = clazz.getMethod("setContentView", int.class);
//                setContentViewMethod.invoke(this, contentView.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析注解
     */
    public void findView() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();//获得Activity中声明的字段
            for (Field field : fields) {
                // 查看这个字段是否有我们自定义的注解类标志的
                if (field.isAnnotationPresent(ContentView.class)) {
                    ContentView inject = field.getAnnotation(ContentView.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true); //反射访问私有成员，必须加上这句
                        field.set(this, this.findViewById(id));//给我们要找的字段设置值
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends Serializable> T getSerializableExtra(String key) {
        return (T) getIntent().getSerializableExtra(key);
    }

    public View inflate(@LayoutRes int layout) {
        return LayoutInflater.from(this).inflate(layout, null);
    }

    public View inflate(@LayoutRes int layout, ViewGroup viewGroup) {
        return LayoutInflater.from(this).inflate(layout, viewGroup, false);
    }


}
