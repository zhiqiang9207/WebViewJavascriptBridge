package com.github.lzyzsd.jsbridge;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.library.R;


/**
 * Created by Andy on 16/5/14.
 */
public class BridgeWebChromeClient extends WebChromeClient {
    private Activity mActivity;
    private WebView mWebView;
    private View mVideoView;
    private CustomViewCallback callback;
    //webview的父视图，用于装载视频view
    private RelativeLayout parent;
    //视频载入前的图片
    private Bitmap videoPoster;
    //全屏时的标题栏
    private LinearLayout titleBar;
    private Handler handler;
    //标题栏淡出
    private Runnable fadeoutRunnable;
    private Runnable orientationRunnable;
    //标题栏的标题
    private TextView fullscreen_title;

    private LayoutInflater inflater;

    public BridgeWebChromeClient(Activity activity){
        mActivity = activity;
    }

    public BridgeWebChromeClient(Activity activity, WebView webView){
        mActivity = activity;
        mWebView = webView;
        handler = new Handler();
        fadeoutRunnable = initTitleBarFadeOutAnimation();
        orientationRunnable = initOrientationRunnable();
        inflater = LayoutInflater.from(mActivity);

        initWebSetting();
    }

    /**
     * 全屏播放配置
     */
    @Override
    public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
        super.onShowCustomView(view, customViewCallback);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        parent = (RelativeLayout) mWebView.getParent();
        parent.removeView(mWebView);
        mVideoView = view;
        callback = customViewCallback;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parent.addView(mVideoView, layoutParams);

        if(titleBar == null){
            setDefaultFullScreenTitleBar();
        }
        if(titleBar.getParent() == null) {
            parent.addView(titleBar, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        titleBar.setVisibility(View.VISIBLE);
        handler.postDelayed(fadeoutRunnable, 3000);
        //3秒之后，使得屏幕监听横竖屏切换，仅在横屏有效
        handler.postDelayed(orientationRunnable, 3000);

        //全屏时标题栏的显示和隐藏
        ((FrameLayout)mVideoView).getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(titleBar.getVisibility() == View.GONE){
                        titleBar.setVisibility(View.VISIBLE);
                    }
                    handler.removeCallbacks(fadeoutRunnable, null);
                    handler.postDelayed(fadeoutRunnable, 3000);
                }
                return false;
            }
        });

        setFullScreenStatusBar();
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();

        if(titleBar != null) {
            parent.removeView(titleBar);
            titleBar.setVisibility(View.GONE);
            handler.removeCallbacks(fadeoutRunnable, null);
        }

        removeOrientationRunnable();
        if(callback != null){
            callback.onCustomViewHidden();
            callback = null;
        }

        //全屏返回的时候，直接addview，可能导致屏幕没有切换过来就开始add，导致出现问题；
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if(parent != null){
                    if(mWebView.getParent() != null) {
                        parent.removeView(mWebView);
                    }
                    parent.addView(mWebView);
                }
            }
        }, 500);

        quitFullScreenStatusBar();
    }

    /**
     * 设置自定义titlebar
     * @param view
     */
    public void setFullScreenTitleBar(LinearLayout view){
        titleBar = view;
    }

    /**
     * 设置默认的TitleBar
     */
    public void setDefaultFullScreenTitleBar(){
        titleBar = (LinearLayout) inflater.inflate(R.layout.fullscreen_titlebar, null);
        fullscreen_title = (TextView) titleBar.findViewById(R.id.fullscreen_text);
        Button button = (Button) titleBar.findViewById(R.id.backward);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideCustomView();
            }
        });
    }

    //视频加载添加默认图标
    @Override
    public Bitmap getDefaultVideoPoster() {
        if(videoPoster != null){
            return videoPoster;
        }
        return super.getDefaultVideoPoster();
    }

    /**
     * 设置默认图标
     * @param bitmap
     */
    public void setDefaultVideoPoster(Bitmap bitmap){
        videoPoster = bitmap;
    }

    //视频加载时进程loading
    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    /**
     * 设置网页的最佳websetting
     */
    private void initWebSetting(){
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setAllowFileAccess(true);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (titleBar != null){
            fullscreen_title.setText(title);
        }
    }

    /**
     * 设置全屏titlebar淡出动画
     * @return
     */
    private Runnable initTitleBarFadeOutAnimation(){
        return new Runnable() {
            @Override
            public void run() {
                if(titleBar!=null) {
                    AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
                    animation.setDuration(700);
                    titleBar.setAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            titleBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    titleBar.startAnimation(animation);
                }
            }
        };
    }

    /**
     * 返回设置屏幕横竖切换runnable
     * @return
     */
    private Runnable initOrientationRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        };
    }

    /**
     * 移除orientationRunnable
     */
    public void removeOrientationRunnable(){
        if(handler != null && orientationRunnable != null){
            handler.removeCallbacks(orientationRunnable);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress >= 100){
            mWebView.getSettings().setBlockNetworkImage(false);
        }
    }

    /**
     *  设置全屏，去除状态栏
     */
    private void setFullScreenStatusBar(){
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏，设置状态栏
     */
    private void quitFullScreenStatusBar(){
        final WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.getWindow().setAttributes(attrs);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
