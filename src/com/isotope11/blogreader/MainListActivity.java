package com.isotope11.blogreader;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

public class MainListActivity extends ListActivity {

  protected ArrayList<BlogPost> mBlogData;
  protected ProgressBar mProgressBar;
  public static final String TAG = MainListActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_list);

    getBlogPosts();
  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);

		// gets the activity's default ActionBar
    ActionBar actionBar = getActionBar();
    actionBar.show();

		return true;
	}

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    BlogPost post = mBlogData.get(position);
    String blogUrl = post.getUrl();
    Intent intent = new Intent(this, BlogViewWebActivity.class);
    intent.setData(Uri.parse(blogUrl));
    startActivity(intent);
  }

  public void getBlogPosts(){
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
	public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.action_refresh:
        getBlogPosts();
    }

		return super.onOptionsItemSelected(item);
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
      BlogPostsAdapterFactory adapterFactory = new BlogPostsAdapterFactory(this, mBlogData, R.layout.post_item);
      setListAdapter(adapterFactory.getAdapter());
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

  private class GetBlogPostsTask extends AsyncTask<Object, Void, ArrayList<BlogPost>> {
    @Override
    protected ArrayList<BlogPost> doInBackground(Object... arg0) {
      return new Blog().getPosts();
    }

    @Override
    protected void onPostExecute(ArrayList<BlogPost> result){
      mBlogData = result;
      handleBlogResponse();
    }
  }

  private class BlogPostsAdapterFactory {
    protected ArrayList<BlogPost> mBlogPosts;
    protected int mLayout;
    protected Context mContext;

    public BlogPostsAdapterFactory(Context context, ArrayList<BlogPost> blogPosts, int layout) {
      mContext = context;
      mBlogPosts = blogPosts;
      mLayout = layout;
    }

    public BaseAdapter getAdapter() {
      return new BaseAdapter() {
        public int getCount() {
          return mBlogPosts.size();
        }

        public Object getItem(int pos) {
          return mBlogPosts.get(pos);
        }

        public long getItemId(int pos) {
          return pos;
        }

        public View getView(int pos, View view, ViewGroup viewGroup) {
          if (view == null) {
            view = View.inflate(mContext, mLayout, null);
          }

          AQuery aq = new AQuery(view);

          BlogPost blogPost = (BlogPost) getItem(pos);

          aq.id(R.id.text1).text(blogPost.getTitle());
          aq.id(R.id.text2).text(blogPost.getAuthor());
          aq.id(R.id.contact_image).image(blogPost.getAuthorAvatar());

          return view;
        }
      };
    }
  }
}
