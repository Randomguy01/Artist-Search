package com.ianwhite.artist_search.activities.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBindings;
import com.ianwhite.artist_search.R;
import com.ianwhite.artist_search.api.SpotifySearch;
import com.ianwhite.artist_search.databinding.ActivityMainBinding;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The main/home screen displayed when the app initially launches. Provides an appbar that expands
 * to show an input field to search for a music artist.
 */
public class MainActivity extends AppCompatActivity {
  /**
   * {@link ViewBindings} binds views from {@link R.layout#activity_main} to their corresponding
   * view objects.
   */
  private ActivityMainBinding mViewBinding;

  /**
   * Adapter for a list of {@link ArtistItem}s.
   */
  private ArtistItemAdapter mArtistItemAdapter;

  /**
   * Defines the initial behavior of the toolbar.
   *
   * If true the {@link SearchView} in {@link R.id#main_toolbar} will be expanded to accept input.
   * If false the {@link SearchView} in {@link R.id#main_toolbar} will be collapsed.
   */
  private boolean mExpandToolbar = false;

  /**
   * The query the user has currently entered in the {@link SearchView}.
   */
  private String mQuery = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate layout
    mViewBinding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(mViewBinding.getRoot());

    // Set toolbar as action bar
    setSupportActionBar(mViewBinding.mainToolbar);

    // Setup RecyclerView
    mArtistItemAdapter = new ArtistItemAdapter(new ArrayList<>());
    mViewBinding.mainResultsRecyclerView.setAdapter(mArtistItemAdapter);

    // Restore state
    if (savedInstanceState != null) {
      // Restore artistItems
      Serializable serializable = savedInstanceState.getSerializable("artistItems");
      if (serializable instanceof ArrayList<?>) {
        ArrayList<?> items = ((ArrayList<?>) serializable);

        //noinspection unchecked
        mArtistItemAdapter.setArtistItems(((ArrayList<ArtistItem>) items));
      }

      // Restore toolbar searchView state
      mExpandToolbar = savedInstanceState.getBoolean("isSearching", false);

      // Restore searchView query
      mQuery = savedInstanceState.getString("query", "");
    }
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    // Store the current list of ArtistItems
    ArrayList<ArtistItem> artistItems = new ArrayList<>(mArtistItemAdapter.getArtistItems());
    outState.putSerializable("artistItems", artistItems);

    // Store the state of the SearchView (expanded/collapsed)
    boolean isSearching = mViewBinding.mainToolbar.getMenu()
        .findItem(R.id.app_bar_search)
        .isActionViewExpanded();
    outState.putBoolean("isSearching", isSearching);

    // Store the current query
    outState.putString("query", mQuery);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate menu
    getMenuInflater().inflate(R.menu.main_appbar_menu, menu);

    // Find search view
    MenuItem searchItem = menu.findItem(R.id.app_bar_search);
    SearchView searchView = ((SearchView) searchItem.getActionView());

    // Expand toolbar if needed
    if (mExpandToolbar) {
      searchView.requestFocus();
      searchItem.expandActionView();
      searchView.onActionViewExpanded();
      searchView.setQuery(mQuery, false);
    }

    // Set query change listener
    searchView.setOnQueryTextListener(mOnQueryTextListener);

    // Menu was inflated
    return true;
  }

  /**
   * Updates the {@link #mArtistItemAdapter} with new search results when the query text changes.
   * When the query is empty a short message instructs the user how to search.
   */
  OnQueryTextListener mOnQueryTextListener = new OnQueryTextListener() {
    @Override
    public boolean onQueryTextChange(String s) {
      // Update the current query field
      mQuery = s;

      // If input exists
      if (s.isEmpty()) {

        // Empty Results
        runOnUiThread(() -> mArtistItemAdapter.setArtistItems(new ArrayList<>()));

        // Show hint/instruction
        mViewBinding.mainHint.setVisibility(View.VISIBLE);
      } else {

        // Search for input
        SpotifySearch.search(s, results -> {

          // UI Operation
          runOnUiThread(() -> {
            // Update results adapter to show new results
            mArtistItemAdapter.setArtistItems(results);
          });

          // Hide hint/instruction
          mViewBinding.mainHint.setVisibility(View.INVISIBLE);
        });
      }

      // Text was not changed
      return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
      // Unused & text was not changed
      return false;
    }
  };

}