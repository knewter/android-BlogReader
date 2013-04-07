package com.isotope11.blogreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class Blog {
  protected URL mUrl;
  public static final String TAG = Blog.class.getSimpleName();

  public Blog() {
    try{
      mUrl = new URL("http://isotope11.com/blog.json");
    } catch (MalformedURLException e){
      logException(e);
    }
  }

  public ArrayList<BlogPost> getPosts(){
    ArrayList<BlogPost> blogPosts = new ArrayList<BlogPost>();
    try {
      String responseData = new GetsUrlContents(mUrl).execute();
      JSONArray jsonArray = new JSONArray(responseData);
      for (int i = 0; i < jsonArray.length(); i++){
        JSONObject post = jsonArray.getJSONObject(i);
        BlogPost blogPost = new BlogPost(post);
        blogPosts.add(blogPost);
      }
    } catch (Exception e) {
      logException(e);
    }
    return blogPosts;
  }

  private void logException(Exception e) {
    Log.e(TAG, "Exception caught", e);
  }
}
