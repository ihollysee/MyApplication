package com.holly.webview;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.holly.user.activity.R;


public class WebViewActivity extends Activity {
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.webview_activity2);

		showWebView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	private void showWebView() { // webView与js交互代码
		try {
			mWebView = new WebView(this);
			setContentView(mWebView);

			mWebView.requestFocus();

			mWebView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int progress) {
					WebViewActivity.this.setTitle("Loading...");
					WebViewActivity.this.setProgress(progress);

					if (progress >= 80) {
						WebViewActivity.this.setTitle("JsAndroid Test");
					}
				}
			});

			mWebView.setOnKeyListener(new View.OnKeyListener() { // webview can
																	// go back
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& mWebView.canGoBack()) {
						mWebView.goBack();
						return true;
					}
					return false;
				}
			});

			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");

			mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");
			mWebView.loadUrl("file:///android_asset/index.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object getHtmlObject() {
		Object insertObj = new Object() {
			public String HtmlcallJava() {
				return "Html call Java";
			}

			public String HtmlcallJava2(final String param) {
				return "Html call Java : " + param;
			}

			public void JavacallHtml() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml()");
						Toast.makeText(WebViewActivity.this, "clickBtn",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			public void JavacallHtml2() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml2('IT-homer blog')");
						Toast.makeText(WebViewActivity.this, "clickBtn2",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		};

		return insertObj;
	}
}
