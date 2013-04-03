package com.isotope11.blogreader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	protected String mBlogPostTitles[] = {};
	protected JSONArray mBlogData;
	protected ProgressBar mProgressBar;
	public static final String TAG = MainListActivity.class.getSimpleName();
	private final String KEY_TITLE = "title";
	private final String KEY_AUTHOR = "user";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

		if(isNetworkAvailable()){
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			mProgressBar.setVisibility(View.VISIBLE);
			getBlogPostsTask.execute();
		} else {
			Toast.makeText(this, "No network available.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			JSONObject jsonPost = mBlogData.getJSONObject(position);
			String blogUrl = jsonPost.getString("url");
			Intent intent = new Intent(this, BlogViewWebActivity.class);
			intent.setData(Uri.parse(blogUrl));
			startActivity(intent);
		} catch (JSONException e) {
			logException(e);
		}
	}

	private void logException(Exception e) {
		Log.e(TAG, "Exception caught", e);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()){
			isAvailable = true;
		}
		return isAvailable;
	}

	protected void handleBlogResponse(){
		mProgressBar.setVisibility(View.INVISIBLE);
		if(mBlogData == null){
			updateDisplayForError();
		} else {
			try {
				ArrayList<HashMap<String, String>> blogPosts = 
						new ArrayList<HashMap<String, String>>();

				for (int i = 0; i < mBlogData.length(); i++){
					JSONObject post = mBlogData.getJSONObject(i);
					String title = post.getString(KEY_TITLE);
					title = Html.fromHtml(title).toString();
					String author = post.getJSONObject(KEY_AUTHOR).getString("to_s");
					author = Html.fromHtml(author).toString();

					HashMap<String, String> blogPost = new HashMap<String, String>();
					blogPost.put(KEY_TITLE, title);
					blogPost.put(KEY_AUTHOR, author);

					blogPosts.add(blogPost);
				}

				String[] keys = { KEY_TITLE, KEY_AUTHOR };
				int[] ids = {android.R.id.text1, android.R.id.text2};
				SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,
						android.R.layout.simple_list_item_2, keys, ids);
				setListAdapter(adapter);
			} catch (JSONException e) {
				logException(e);
			}
		}
	}

	private void updateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();

		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}

	private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Object... arg0) {
			JSONArray jsonResponse = null;
			try {
				URL blogFeedUrl = new URL("http://isotope11.com/blog.json");

				String responseData = new GetsUrlContents(blogFeedUrl).execute();
				jsonResponse = new JSONArray(responseData);
			} catch (Exception e) {
				logException(e);
			}
			return jsonResponse;
		}

		@Override
		protected void onPostExecute(JSONArray result){
			mBlogData = result;
			handleBlogResponse();
		}
	}
}