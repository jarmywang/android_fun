package com.example.user.myapplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang on 2015/10/22.
 */
public class JavaFun {
    public static String JavaFun = "JavaFun";

    public static void main(String args[]) {
        getStrings();
        getStrings2();
        getString3();
        getString4();
        getString5();
        getString6();
        getString7();
        getDouble();
        number();

        JavaFun JavaFun = new JavaFun();
        JavaFun.JavaFun();
    }

    static void JavaFun() {
        System.out.println(JavaFun);
    }

    public static void getStrings() {
        String str = "rrwerqq84461376qqasfdasdfrrwerqq84461377qqasfdasdaa654645aafrrwerqq84461378qqasfdaa654646aaasdfrrwerqq84461379qqasfdasdfrrwerqq84461376qqasfdasdf";
        Pattern p = Pattern.compile("qq(.*?)qq");
        Matcher m = p.matcher(str);
        ArrayList<String> strs = new ArrayList<String>();
        while (m.find()) {
            strs.add(m.group(1));
        }
        for (String s : strs) {
            System.out.println(s);
        }
    }

    public static void getStrings2() {
        String str = "新用户注册后可获得0.3元红包";
        Pattern p = Pattern.compile("\\D(\\d+.*)元");
        Matcher m = p.matcher(str);
        while (m.find()) {
            System.out.println("=======================m.groupCount=" + m.groupCount());
            String num = m.group(1);
            System.out.println("num=" + num);
            String s1 = str.substring(0, str.indexOf(num));
            String s2 = num + str.substring(str.indexOf(num) + num.length(), str.indexOf(num) + num.length() + 1);
            String s3 = str.substring(str.indexOf(num) + num.length() + 1, str.length());

            System.out.println("s1=" + s1);
            System.out.println("s2=" + s2);
            System.out.println("s3=" + s3);
            System.out.println("===============================");
        }
    }

    public static void getString3() {
        String js = "http://XXXX.js;";
        System.out.println("js=" + js);
        System.out.println("js_new=" + js.replace("dev/", ""));

        System.out.println("===============================");
        String sn = "1056034";
        if ("0".equals(sn.substring(sn.length() - 3, sn.length() - 2))) {
            sn = sn.substring(0, sn.length() - 3) + "k";
        } else {
            sn = sn.substring(0, sn.length() - 3) + "." + sn.substring(sn.length() - 3, sn.length() - 2) + "k";
        }
        System.out.println("sn=" + sn);
        System.out.println("===============================");
    }

    public static void getString4() {
        int a = 3;
        System.out.println(String.format("%02d", a));
        double b = 3.329321;
        System.out.println(String.format("%.2f", b));
        System.out.println(String.format("%.1f", 86777 / 10000f));
        System.out.println(String.format("%,.0f", 12345678f));
        System.out.println("===============================");
    }

    public static void getString5() {
        int INTERFACE_TRANSACTION = ('_' << 24) | ('N' << 16) | ('T' << 8) | 'F';
        System.out.println("INTERFACE_TRANSACTION=" + INTERFACE_TRANSACTION);
        int int1 = 2 | 8;
        int int2 = int1 << 2;
        int int3 = int1 >> 2;
        System.out.println("int1=" + int1 + ",int2=" + int2 + ",int3=" + int3);
        System.out.println("===============================");
    }

    public static void getString6() {
        StringBuffer s = new StringBuffer("good");
        StringBuffer s2 = new StringBuffer("bad");
        test(s, s2);
        System.out.println(s);//9
        System.out.println(s2);//10
        System.out.println("===============================");
    }

    public static void getString7() {
        String s = "cfs://ddd/aaa/15/10/as";
        String[] sf = s.split("/");
        for (int i = 0; i < sf.length; i++) {
            System.out.println("sf[" + i + "]=" + sf[i]);
        }
        System.out.println("===============================");
    }

    public static void getDouble() {
        double amount = 2553.18d;
        int intNormal = (int) (amount * 100);
        int intMathRound = (int) (Math.round(amount * 100));
        int mul = (int) mul(amount, 100);
        System.out.println("getDouble..amount*100=" + (amount * 100));
        System.out.println("getDouble..intNormal=" + intNormal);
        System.out.println("getDouble..intMathRound=" + intMathRound);
        System.out.println("getDouble..mul=" + mul);
        System.out.println("===============================");
    }

    static void number() {
        diff("81000081e9ff0000", "01800049e1ff0000");//5
        diff("0b1b99c101400527", "0b1b999f0300052f");//5
        diff("fdff47e80020231b", "ffff4fee0408031b");//7
        diff("ffffcfb80028031b", "0b1b99c101400527");//14
        diff("81000081e9ff0000", "ffffcfb80028031b");//15
    }

    static void diff(String s1, String s2) {
        char[] s1s = s1.toCharArray();
        char[] s2s = s2.toCharArray();
        int diffNum = 0;
        for (int i = 0; i<s1s.length; i++) {
            if (s1s[i] != s2s[i]) {
                diffNum++;
            }
        }
        System.out.println("diffNum="+diffNum);
    }

    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    static void test(StringBuffer s, StringBuffer s2) {
        System.out.println(s);//1
        System.out.println(s2);//2
        s2 = s;//3
        s = new StringBuffer("new");//4
        System.out.println(s);//5
        System.out.println(s2);//6
        s.append("hah");//7
        s2.append("hah");//8
    }


}
