package com.example.user.horizontalgallery;


import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache {

  private static MemoryCache ourInstance = new MemoryCache();

  public LruCache<String, Bitmap> retainedCache;

  private MemoryCache() {
  }

  public static MemoryCache getInstance() {
    return ourInstance;
  }

}
