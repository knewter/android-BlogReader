package com.isotope11.blogreader;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

public class BlogPost {
  protected JSONObject mData;
  private final String KEY_TITLE = "title";
  private final String KEY_AUTHOR = "user";
  private final String TAG = BlogPost.class.toString();

  public BlogPost(JSONObject postData){
    mData = postData;
  }

  public String getTitle(){
    String title = "";
    try {
      title = mData.getString(KEY_TITLE);
      title = Html.fromHtml(title).toString();
    } catch (JSONException e) {
      logException(e);
    }
    return title;
  }

  public String getAuthor(){
    String author = "";
    try {
      author = mData.getJSONObject(KEY_AUTHOR).getString("to_s");
      author = Html.fromHtml(author).toString();
    } catch (JSONException e) {
      logException(e);
    }
    return author;
  }

  public String getUrl(){
    String url = "";
    try {
      url = mData.getString("url");
    } catch (JSONException e) {
      logException(e);
    }
    return url;
  }

	private void logException(Exception e) {
		Log.e(TAG, "Exception caught", e);
	}
}
