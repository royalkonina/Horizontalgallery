package com.example.user.horizontalgallery;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
  private final WeakReference<ImageView> imageViewReference;
  private int data = 0;
  private OnUpdateMemCacheListener onUpdateMemCacheListener;

  public BitmapWorkerTask(ImageView imageView, OnUpdateMemCacheListener onUpdateMemCacheListener) {
    imageViewReference = new WeakReference<ImageView>(imageView);
    this.onUpdateMemCacheListener = onUpdateMemCacheListener;
  }

  @Override
  protected Bitmap doInBackground(Integer... params) {
    data = params[0];
    int reqWidth = params[1];
    int reqHeight = params[2];
    Bitmap bitmap = decodeSampledBitmapFromURL(data, reqWidth, reqHeight);
    onUpdateMemCacheListener.addToMemCache(data + " " + reqWidth + " " + reqHeight, bitmap);
    return bitmap;
  }

  public static Bitmap decodeSampledBitmapFromURL(int resId, int reqWidth, int reqHeight) {
    Bitmap bitmap = null;

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    try {
      URL url = new URL(GalleryActivity.IMAGES_URL[resId]);
      BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
      Log.d("BitmapSizeAfterSampling", bitmap.getHeight() + "x" + bitmap.getWidth());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bitmap;
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight
              && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    if (isCancelled()) {
      bitmap = null;
    }
    if (bitmap != null) {
      final ImageView imageView = imageViewReference.get();
      final BitmapWorkerTask bitmapWorkerTask =
              getBitmapWorkerTask(imageView);
      if (this == bitmapWorkerTask && imageView != null) {
        imageView.setImageBitmap(bitmap);
      }
    }
  }

  private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    if (imageView != null) {
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
        final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
        return asyncDrawable.getBitmapWorkerTask();
      }
    }
    return null;
  }

  public static boolean cancelPotentialWork(int data, ImageView imageView) {
    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

    if (bitmapWorkerTask != null) {
      final int bitmapData = bitmapWorkerTask.data;
      if (bitmapData != data) {
        // Cancel previous task
        bitmapWorkerTask.cancel(true);
      } else {
        // The same work is already in progress
        return false;
      }
    }
    // No task associated with the ImageView, or an existing task was cancelled
    return true;
  }

}