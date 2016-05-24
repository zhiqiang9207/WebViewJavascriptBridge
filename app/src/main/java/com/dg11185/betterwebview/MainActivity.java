package com.dg11185.betterwebview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.lzyzsd.jsbridge.BridgeWebChromeClient;
import com.github.lzyzsd.jsbridge.BridgeWebView;

public class MainActivity extends Activity {
    private BridgeWebView bwv;
    private BridgeWebChromeClient chromeClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BridgeWebViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
