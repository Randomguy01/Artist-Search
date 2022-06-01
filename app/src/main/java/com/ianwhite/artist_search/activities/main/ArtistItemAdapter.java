package com.ianwhite.artist_search.activities.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ianwhite.artist_search.R;
import com.ianwhite.artist_search.activities.artist.ArtistActivity;
import java.util.List;

/**
 * Creates {@link ArtistItem}s for the ArtistRecyclerView.
 */
public class ArtistItemAdapter extends RecyclerView.Adapter<ArtistItemAdapter.ViewHolder> {
  /**
   * The {@link ArtistItem}s to display.
   */
  private final List<ArtistItem> mArtistItems;

  /**
   * Constructor takes all fields as parameter.
   */
  public ArtistItemAdapter(List<ArtistItem> artistItems) {
    mArtistItems = artistItems;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // Inflate artist_item layout
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.artist_item, parent, false);

    // Create ViewHolder for layout
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Get current ArtistItem
    ArtistItem artistItem = mArtistItems.get(position);

    // Display Artist's name
    holder.getArtistName().setText(artistItem.getName());

    // Load Artist's Photo
    Glide.with(holder.getView())
        .load(artistItem.getImageUrl())
        .into(holder.getArtistPhoto());

    // On click start ArtistActivity
    holder.getView().setOnClickListener(v -> {
      Intent activityIntent = new Intent(holder.getView().getContext(), ArtistActivity.class);
      activityIntent.putExtra("artistItem", artistItem);
      holder.getView().getContext().startActivity(activityIntent);
    });

  }

  @Override
  public int getItemCount() {
    return mArtistItems.size();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void setArtistItems(List<ArtistItem> items) {
    mArtistItems.clear();
    mArtistItems.addAll(items);

    // Notify adapter to rebuild
    notifyDataSetChanged();
  }

  public List<ArtistItem> getArtistItems() {
    return mArtistItems;
  }

  /**
   * Provides access to {@link R.layout#artist_item} views.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    /**
     * The root view of the ViewHolder.
     */
    private final View mView;

    /**
     * The {@link TextView} displaying the artist's name.
     */
    private final TextView mArtistName;

    /**
     * The {@link ImageView} displaying the artist's profile photo.
     */
    private final ImageView mArtistPhoto;

    /**
     * Constructor binds fields using {@link #itemView}.
     */
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      mArtistName = itemView.findViewById(R.id.main_artistItem_name);
      mArtistPhoto = itemView.findViewById(R.id.main_artistItem_profile);
    }

    public View getView() {
      return mView;
    }

    public ImageView getArtistPhoto() {
      return mArtistPhoto;
    }

    public TextView getArtistName() {
      return mArtistName;
    }
  }
}
