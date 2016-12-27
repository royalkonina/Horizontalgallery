package com.example.user.horizontalgallery;


import android.graphics.Bitmap;

public interface OnUpdateMemCacheListener {
  void addToMemCache(String key, Bitmap bitmap);
}
