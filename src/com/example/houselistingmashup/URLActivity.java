package com.example.houselistingmashup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class URLActivity extends Activity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra(ZillowTab.EXTRA_MESSAGE1);
		
		 WebView webView = (WebView)findViewById(R.id.webView);
		 
	        webView.setInitialScale(1);
	        webView.getSettings().setJavaScriptEnabled(true);
	        webView.getSettings().setLoadWithOverviewMode(true);
	        webView.getSettings().setUseWideViewPort(true);
	        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	        webView.setScrollbarFadingEnabled(false);
	         
	        webView.loadUrl(url);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.url, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
