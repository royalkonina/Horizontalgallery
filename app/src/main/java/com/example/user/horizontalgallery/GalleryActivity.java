package com.example.user.horizontalgallery;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryActivity extends AppCompatActivity {
  private boolean isLandscape;
  private GridView horizontalGridView;
  private GridViewAdapter adapter;
  public static String[] IMAGES_URL;
  public static final int ROWS_LANDSCAPE = 2;
  public static final int ROWS_PORTRAIT = 4;
  public static final String PLACEHOLDER_KEY = "PLACEHOLDER";
  public int reqWidth;
  public int reqHeight;
  private LruCache<String, Bitmap> memoryCache;


  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);
    fill_IMAGES_URL(30);

    setupMemCache();

    isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;


    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    horizontalGridView = (GridView) findViewById(R.id.horizontal_gridView);
    if (isLandscape) {
      horizontalGridView.setNumColumns(ROWS_LANDSCAPE);
      reqWidth = (metrics.widthPixels - (ROWS_LANDSCAPE - 1) * horizontalGridView.getVerticalSpacing()) / ROWS_LANDSCAPE;
    } else {
      horizontalGridView.setNumColumns(ROWS_PORTRAIT);
      reqWidth = (metrics.widthPixels - (ROWS_PORTRAIT - 1) * horizontalGridView.getVerticalSpacing())/ ROWS_PORTRAIT;
    }
    reqHeight = reqWidth;

    final Bitmap placeholder = decodeSampledPlaceholder();
    adapter = new GridViewAdapter(reqWidth, reqHeight);
    adapter.setContext(this);
    adapter.setOnLoadBitmapListener(new OnLoadBitmapListener() {
      @Override
      public void loadBitmap(int position, ImageView imageView) {
        final String imageKey = position + " " + reqWidth + " " + reqHeight;
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
          imageView.setImageBitmap(bitmap);
        } else {
          if (BitmapWorkerTask.cancelPotentialWork(position, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, new OnUpdateMemCacheListener() {
              @Override
              public void addToMemCache(String key, Bitmap bitmap) {
                addBitmapToMemoryCache(key, bitmap);
              }
            });
            final AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), placeholder, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(position, reqWidth, reqHeight);
          }
        }
      }
    });
    horizontalGridView.setAdapter(adapter);
  }

  private void setupMemCache() {
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 4;

    RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getFragmentManager());
    memoryCache = retainFragment.retainedCache;
    if (memoryCache == null) {
      memoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
          return bitmap.getByteCount() / 1024;
        }
      };
      retainFragment.retainedCache = memoryCache;
    }
  }

  private void fill_IMAGES_URL(int count) {
    IMAGES_URL = new String[count];
    for (int i = 0; i < count; i++) {
      IMAGES_URL[i] = "http://images.memes.com/meme/" + (1299491 - i);
    }
  }

  public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
    if (getBitmapFromMemCache(key) == null) {
      memoryCache.put(key, bitmap);
    }
  }

  @Override
  protected void onDestroy() {
    memoryCache.remove(PLACEHOLDER_KEY);
    super.onDestroy();
  }

  public Bitmap getBitmapFromMemCache(String key) {
    return memoryCache.get(key);
  }

  public Bitmap decodeSampledPlaceholder() {
    Bitmap placeholder = null;
    if (memoryCache != null) {
      placeholder = memoryCache.get(PLACEHOLDER_KEY);
    }
    if (placeholder == null) {
      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(getResources(), R.drawable.placeholder, options);
      // Calculate inSampleSize
      Log.d("placeholder's size", reqWidth + " " + reqHeight);
      options.inSampleSize = BitmapWorkerTask.calculateInSampleSize(options, reqWidth, reqHeight);
      Log.d("placeholder's sampling", String.valueOf(options.inSampleSize));
      // Decode placeholder with inSampleSize set
      options.inJustDecodeBounds = false;
      placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder, options);
      memoryCache.put(PLACEHOLDER_KEY, placeholder);
    }
    return placeholder;
  }


}
