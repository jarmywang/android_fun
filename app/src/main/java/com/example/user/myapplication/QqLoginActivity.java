package com.example.user.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * Created by User on 2015/10/9.
 */
public class QqLoginActivity extends Activity {

    public static String TAG = "QqLoginActivity";

    public static String LOGIN_BOX = "http://m.qzone.com";

    public static String ua = "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X;en-us) "
            + "AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334bSafari/531.21.10"; // PC端ua

    WebView wv_login;
    WebSettings ws;
    Handler hd;

    private String qq="123456";
    private String pwd="ooooo";
    private String sid="";
    private String skey = ""; // 不带@符号传给服务端
    private String vercode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_login);
        wv_login = (WebView)findViewById(R.id.wv);
        ws = wv_login.getSettings();
        hd = new Handler(getMainLooper());
        initWebLogin();
    }


    private void initWebLogin(){
        backstageLogin();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setBuiltInZoomControls(true);
        ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
        ws.setAllowFileAccess(true);
        ws.setUserAgentString(ua);
        wv_login.addJavascriptInterface(new InJSLocalObj(), "local_getpwd_obj");
        wv_login.setClickable(true);
        wv_login.setWebChromeClient(new WebChromeClient());
        hr365test();
//        openQqLogin();
    }


    private void hr365test(){
        wv_login.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webview_login(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:" + getFromAssets("searchInsurance.js"));
                view.loadUrl("javascript:alert('111111')");
            }
        });
        String searchInsuranceUrl = "http://gzlss.hrssgz.gov.cn:7001/cas/login?" +
                "service=http%3A%2F%2Fgzlss.hrssgz.gov.cn%3A7001%2Fgzlss_web%2Fbusiness%2Fauthentication%2Flogin.xhtml";
        wv_login.loadUrl(searchInsuranceUrl);
    }

    private void openQqLogin(){
        wv_login.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webview_login(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
//                getpwd();
                view.loadUrl(url);
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "0602..shouldOverrideUrlLoading.url=" + url);
                        if (url.contains("http://m.qzone.com/infocenter")) { // http://user.qzone.qq.com
                            Log.i(TAG, "0602.." + qq + "登录成功！！！");
//                            if (url.contains("http://user.qzone.qq.com/" + qq)) {
                            Log.i(TAG, "0602..成功打开自己主页");
//                                ws.setUserAgentString("");
                            view.loadUrl("http://user.qzone.qq.com/" + qq);
                            Log.i(TAG, "0602..成功打开自己主页后请求："+"http://user.qzone.qq.com/" + qq);
//                            }
                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if ((TextUtils.isEmpty(sid) || TextUtils.isEmpty(skey)) && !TextUtils.isEmpty(qq)) {
                                        Log.i(TAG, "0602..没拿到sid，再次登录2");
//                                        ws.setUserAgentString("");
                                        view.loadUrl("http://user.qzone.qq.com/" + qq);
                                        Log.i(TAG, "0602..成功打开自己主页后请求：" + "http://user.qzone.qq.com/" + qq);
                                    }
                                }
                            }, 3000);
                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if ((TextUtils.isEmpty(sid) || TextUtils.isEmpty(skey)) && !TextUtils.isEmpty(qq)) {
                                        Log.i(TAG, "0602..没拿到sid，不再次登录3");
                                        backstageLogin();
                                        wv_login.loadUrl(LOGIN_BOX);
                                    }
                                }
                            }, 6000);
                        }
                    }
                }, 1000);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "0602..onPageFinished--url=" + url);
                getSkey();
                view.loadUrl("javascript:" + getFromAssets("alert.js"));
//				webview_login(url, wv_login); // 会无限循环打开url
            }
        });
        wv_login.loadUrl(LOGIN_BOX);
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(qq) && !TextUtils.isEmpty(pwd))
                    wv_login.loadUrl("javascript:document.getElementById('u').value=" + qq + ";"
                            + "javascript:document.getElementById('p').value='" + pwd + "';");
            }
        }, 800);
    }

    private void webview_login(String url){
        Log.i(TAG, "0602..webview_login..url="+url);
        if(url.contains("http://m.qzone.com/profile_get?format=json&sid=")){ // http://m.qzone.com/profile?sid=
            try {
//                backstageLogin();
                Log.i(TAG, "0602..webview_login成功打开自己主页m端，url="+url);
                sid = url.substring(url.indexOf("sid=") + "sid=".length(), url.indexOf("&hostuin"));
                sid = URLDecoder.decode(sid);
                Log.i(TAG, "0602..webview_login成功打开自己主页m端，sid="+sid);
            } catch (Exception e) {
                e.printStackTrace();
                wv_login.loadUrl(LOGIN_BOX);
            }
        }
    }

    private void getpwd(){
        Log.i(TAG,"0602..getpwd()");
        if(wv_login!=null) {
            wv_login.loadUrl("javascript:window.local_getpwd_obj.getpwd("
                    + "document.getElementById('u').value,document.getElementById('p').value);");
        }
    }
    private void getSkey(){
        if(wv_login!=null){
            Log.d(TAG,"0727..getSkey...");
            wv_login.loadUrl("javascript:window.local_getpwd_obj.getSkey('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        }
    }

    final class InJSLocalObj{
        @JavascriptInterface
        public void getpwd(String w_qq,String w_pwd){
            if(!TextUtils.isEmpty(w_qq) && !TextUtils.isEmpty(w_qq)){
                Log.i(TAG, "0602..qq=" + qq + ",pwd=" + pwd);
                qq = w_qq;
                pwd = w_pwd;
            }
        }
        @JavascriptInterface
        public void getSkey(String html) {
            if(TextUtils.isEmpty(html)) return;
            if(html.contains("<title>登录成功</title>")){
                String mCookie = CookieManager.getInstance().getCookie(LOGIN_BOX);
                if(mCookie!=null && mCookie.contains("skey=@")){
                    skey = mCookie.substring(mCookie.indexOf("skey=@")+6, mCookie.indexOf("skey=@")+6+9); // 去掉@符号
                    Log.i(TAG, "0727..小号skey="+skey);
                }
            }
        }
    }

    /**
     * 清除缓存并登出,只要清除掉cookie就自动跳转到登陆框
     */
    private void backstageLogin(){
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
//        wv_login.loadUrl(QUrl.LOGIN_BOX);
    }

    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            Log.i(TAG, "getFromAssets.Result=" + Result);
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
