package com.isotope11.blogreader;

import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BlogViewWebActivity extends Activity {

	protected String mUrl;
	protected Uri mBlogUri;
	protected String mBlogPostData;
	protected ProgressBar mProgressBar;
	protected String TAG = BlogViewWebActivity.class.toString();

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_view_web);
		Intent intent = getIntent();
		mBlogUri = intent.getData();
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setVisibility(View.VISIBLE);
		GetBlogPostTask getBlogPostTask = new GetBlogPostTask();
		getBlogPostTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog_view_web, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_share) {
			sharePost();
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void handleBlogPostResponse(){
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		String localData = new String(mBlogPostData);
		String customCSS = "#top_area { display: none; }\n" +
				".work_content_right.recent { display: none !important; }\n" +
				"p.supplemental { display: none; }\n" +
				".share { display: none; }\n" +
				".work_content_left.blog_show .blog_post .blog_content { margin: 0 !important; }\n" +
				".work_content_left { width: 100% !important; }" +
				"span.tags { margin: 0; }\n" +
				".author_bio { margin: 0; }\n" +
				"footer { display: none; }\n";
		localData = localData.replaceAll("</head>", "<style>" + customCSS + "</style></head>");

		String customJS = "progress.hide();";
		localData = localData.replaceAll("</body>", "<script>" + customJS + "</script></body>");

		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		ProgressBarHider progressBarHider = new ProgressBarHider(mProgressBar);
		webView.addJavascriptInterface(progressBarHider, "progress");

    webView.loadDataWithBaseURL(mBlogUri.toString(), localData, "text/html", "UTF-8", null);
	}

	public void sharePost() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
		startActivity(Intent.createChooser(shareIntent, getString(R.string.share_choose_title)));
	}

	private class GetBlogPostTask extends AsyncTask<Object, Void, String> {
		@Override
		protected String doInBackground(Object... arg0) {
			URL blogUrl;
			String responseData = "";
			try {
				blogUrl = new URL(mBlogUri.toString());

				responseData = new GetsUrlContents(blogUrl).execute();
			} catch (MalformedURLException e) {
				Log.d(TAG, "Exception Caught", e);
			}
			return responseData;
		}

		@Override
		protected void onPostExecute(String response){
			mUrl = mBlogUri.toString();
			mBlogPostData = response;
			handleBlogPostResponse();
		}
	}

	private class ProgressBarHider{
		ProgressBar mProgressBar;
		public ProgressBarHider(ProgressBar progress){
			mProgressBar = progress;
		}

		@SuppressWarnings("unused")
		@JavascriptInterface
		public void hide(){
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);
		}
	}
}
