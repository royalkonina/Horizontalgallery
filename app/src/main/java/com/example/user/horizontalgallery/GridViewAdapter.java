package com.example.user.horizontalgallery;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

  private OnLoadBitmapListener onLoadBitmapListener;
  private Context context;

  public void setOnLoadBitmapListener(OnLoadBitmapListener onLoadBitmapListener) {
    this.onLoadBitmapListener = onLoadBitmapListener;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return GalleryActivity.IMAGES_URL.length;
  }

  @Override
  public Object getItem(int position) {
    return GalleryActivity.IMAGES_URL[position];
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    ImageView imageView;
    if (view == null) {
      imageView = new ImageView(context);
      imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      int length = 100;
      imageView.setMaxHeight(length);
      imageView.setMaxWidth(length);
    } else {
      imageView = (ImageView) view;
    }
    onLoadBitmapListener.loadBitmap(position, imageView);
    return imageView;
  }
}