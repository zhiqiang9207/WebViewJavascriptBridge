# WebViewJavascriptBridge
基于WebViewJavascriptBridge再次封装的webview框架；
原WebViewJavascriptBridge地址：https://github.com/lzyzsd/JsBridge

在这个框架上面做了以下的改动：
1、不在BridgeWebView里面构造方法里面自动setBridgeWebViewClient，以便后面自定BridgeWebViewClient；
2、增加BridgeWebChromeClient，增加视频全屏相关功能；

Notice:
视频全屏的功能并不是所有机子都成功，试过三星、LG、华为、小米、魅族，目前只有小米的Flymeos 5.1.5.0A有问题，不确定其他机子会不会有同样的问题。
