package com.ianwhite.artist_search.activities.artist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.ianwhite.artist_search.activities.main.ArtistItem;
import com.ianwhite.artist_search.databinding.ActivityArtistBinding;
import java.text.DecimalFormat;

/**
 * Displays information about an artist using {@link ArtistItem}. Displays the artist's name, photo,
 * and the number of followers the artist has.
 *
 * An {@link ArtistItem} must be passed to this activity through the launching {@link Intent} as an
 * extra with the name 'artistItem'.
 */
public class ArtistActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate content view
    ActivityArtistBinding viewBinding = ActivityArtistBinding.inflate(getLayoutInflater());
    setContentView(viewBinding.getRoot());

    // Setup back button
    viewBinding.artistAppBar.setNavigationOnClickListener(v -> finish());

    // Get artist item passed from previous activity
    ArtistItem artistItem = ((ArtistItem) getIntent().getSerializableExtra("artistItem"));

    // Return if no artist data was provided
    if (artistItem == null) {
      Toast.makeText(this, "Artist Information Missing", Toast.LENGTH_SHORT).show();
      return;
    }

    // Set AppBar title to the artist's name
    viewBinding.artistAppBar.setTitle(artistItem.getName());

    // Load profile photo
    Glide.with(this)
        .load(artistItem.getImageUrl())
        .into(viewBinding.artistImage);

    // Show formatted number of followers
    DecimalFormat formatter = new DecimalFormat("###,###,###,##0");
    viewBinding.artistFollowers.setText(
        String.format("%s Followers", formatter.format(artistItem.getFollowers()))
    );
  }
}