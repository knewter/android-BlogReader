package com.isotope11.blogreader;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

public class RecentBlogPostWidgetProvider extends AppWidgetProvider {
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    Timer timer = new Timer();
    int frequency = 60 * 10 * 1000; // Every 10 minutes
    timer.scheduleAtFixedRate(new UpdateRecentPost(context, appWidgetManager), 1, frequency);
  }

  private class UpdateRecentPost extends TimerTask {
    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;

    public UpdateRecentPost(Context context, AppWidgetManager appWidgetManager) {
      this.appWidgetManager = appWidgetManager;
      remoteViews = new RemoteViews(context.getPackageName(), R.layout.recent_blog_post_widget);
      thisWidget = new ComponentName(context, RecentBlogPostWidgetProvider.class);
    }

    @Override
    public void run() {
      BlogPost post = getMostRecentBlogPost();
      remoteViews.setTextViewText(R.id.textView1, post.getTitle());
      appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    protected BlogPost getMostRecentBlogPost(){
      Blog blog = new Blog();
      ArrayList<BlogPost> posts = blog.getPosts();
      return posts.get(0);
    }
  }
}
