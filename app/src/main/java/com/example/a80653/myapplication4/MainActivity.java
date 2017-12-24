package com.example.a80653.myapplication4;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends Activity {

    private View mErrorView;
    private WebView mWebView;
    private Dialog progressDialog;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Loading Dialog 对象
        progressDialog = new Dialog(this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("加载中...");

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // 注入JS 调用刷新方法
        mWebView.addJavascriptInterface(this, "local_obj");

        // 返回按键事件监听
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {  //表示按返回键
                        String url = mWebView.getUrl();
                        String action = url.substring(url.lastIndexOf("/"));
                        if("/main".equals(action)){
                            finish();
                        }else{
                            mWebView.goBack();
                        }
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

        // 监听屏幕长按事件
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient(){

            // 加载页面出错显示本地提示页面
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressDialog.dismiss();
                mWebView.loadUrl("file:///android_asset/ui/index.html");
            }

            // webView 开始加载页面 显示Loading
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            // webView 加载页面结束 取消Loading
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }
        });

        mWebView.loadUrl("http://139.224.71.144:20080/cylinder/appui/index.html#/main");
    }

    // 刷新重新加载页面
    @JavascriptInterface
    public void reLoadPage() {

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("http://139.224.71.144:20080/cylinder/appui/index.html#/login");
            }
        });
    }
}