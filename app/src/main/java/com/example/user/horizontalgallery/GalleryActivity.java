package com.example.user.horizontalgallery;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryActivity extends AppCompatActivity {
  private boolean isLandscape;
  private GridView horizontalGridView;
  private GridViewAdapter adapter;
  // public static final String[] IMAGES_URL = {"http://images.memes.com/meme/1299491", "http://images.memes.com/meme/1299490", "http://images.memes.com/meme/1299489", "http://images.memes.com/meme/1299488", "http://images.memes.com/meme/1299487"};
  public static String[] IMAGES_URL;
  public static final int rowsLandscape = 2;
  public static final int rowsPortrait = 4;
  private LruCache<String, Bitmap> memoryCache;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);
    fill_IMAGES_URL(30);

    isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    horizontalGridView = (GridView) findViewById(R.id.horizontal_gridView);

    adapter = new GridViewAdapter();
    adapter.setContext(this); //can????
    adapter.setOnLoadBitmapListener(new OnLoadBitmapListener() {
      @Override
      public void loadBitmap(int position, ImageView imageView) {
        final String imageKey = String.valueOf(position);
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
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), /*BitmapFactory.decodeResource(getResources(), R.drawable.placeholder)*/null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(position);
          }
        }
      }
    });

    horizontalGridView.setAdapter(adapter);
    int countImages = IMAGES_URL.length;
    // horizontalGridView.setNumColumns(isLandscape ? (countImages + rowsLandscape - 1) / rowsLandscape : (countImages + rowsPortrait - 1) / rowsPortrait);
    horizontalGridView.setNumColumns(isLandscape ? rowsLandscape : rowsPortrait);
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

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

  public Bitmap getBitmapFromMemCache(String key) {
    return memoryCache.get(key);
  }


}
