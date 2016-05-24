package com.dg11185.betterwebview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import com.github.lzyzsd.jsbridge.BridgeWebChromeClient;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;

public class BridgeWebViewActivity extends Activity {
    private BridgeWebView bwv;
    private BridgeWebChromeClient chromeClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge);

        bwv = (BridgeWebView) findViewById(R.id.bridge);
        bwv.setWebViewClient(new BridgeWebViewClient(bwv));
        chromeClient = new BridgeWebChromeClient(this, bwv);
        //设置加载前的图片
//        chromeClient.setDefaultVideoPoster(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        bwv.setWebChromeClient(chromeClient);

        //app缓存
//        bwv.getSettings().setAppCacheEnabled(true);
//        bwv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //加速webview加载，先不加载图片，在onProgressChanged＝100或者onpagefinish的时候再设置为false，这样打开网页会快一点；
//        bwv.getSettings().setBlockNetworkImage(true);

//        bwv.loadUrl("http://m.sp.uczzd.cn/webapp/webview/article/news.html?app=uc-iflow&aid=5407078403109189719&cid=100&zzd_from=uc-iflow&uc_param_str=dndsfrvesvntnwpfgi&recoid=13487156982886247311&rd_type=reco&pagetype=share");
//        bwv.loadUrl("http://m.sp.uczzd.cn/webapp/webview/article/news.html?app=uc-iflow&aid=3479470807939680455&cid=100&zzd_from=uc-iflow&uc_param_str=dndsfrvesvntnwpfgi&recoid=12105221635696960141&rd_type=reco&pagetype=share");
        bwv.loadUrl("file:///android_asset/webpage/fullscreenVideo.html");
//        bwv.loadUrl("http://www.iqiyi.com/w_19rtg5l1k1.html");
        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            chromeClient.onHideCustomView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            chromeClient.onHideCustomView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bwv.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bwv.onResume();
    }
}
