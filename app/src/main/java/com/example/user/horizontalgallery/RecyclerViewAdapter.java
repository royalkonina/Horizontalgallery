package com.example.user.horizontalgallery;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
  private int reqWidth;
  private int reqHeight;
  private OnLoadBitmapListener onLoadBitmapListener;

  public void setOnLoadBitmapListener(OnLoadBitmapListener onLoadBitmapListener) {
    this.onLoadBitmapListener = onLoadBitmapListener;
  }


  public RecyclerViewAdapter(int reqWidth, int reqHeight) {
    super();
    this.reqWidth = reqWidth;
    this.reqHeight = reqHeight;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_imageitem, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    onLoadBitmapListener.loadBitmap(position, holder.imageView);
  }

  @Override
  public int getItemCount() {
    return  GalleryActivity.IMAGES_URL.length;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;

    public ViewHolder(final View itemView) {
      super(itemView);
      imageView = (ImageView) itemView.findViewById(R.id.imageView);
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setLayoutParams(new LinearLayout.LayoutParams(reqWidth, reqWidth));
    }
  }
}
