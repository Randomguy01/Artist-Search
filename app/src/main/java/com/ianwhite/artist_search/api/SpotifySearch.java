package com.ianwhite.artist_search.api;

import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import com.ianwhite.artist_search.activities.main.ArtistItem;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides search functionality for Spotify API.
 */
public class SpotifySearch {
  private static final String TAG = SpotifySearch.class.getSimpleName();

  // Basic authentication credentials
  private static final String CLIENT_ID = "6e19f66fadf947be9bf35633d533fd0e";
  private static final String CLIENT_SECRET = "de18465928c848eeaf7fda1f57a31641";

  /**
   * Token used to make Spotify API calls.
   */
  private static String sToken;

  /**
   * Triggered when an authentication operation is completed.
   */
  private interface AuthListener {
    /**
     * Called when an authentication operation is completed with success or failure.
     *
     * @param isAuthenticated if the authentication operation was successful.
     */
    void onAuthenticated(boolean isAuthenticated);
  }

  /**
   * Triggered when a search is completed.
   */
  public interface SearchListener {
    /**
     * Called when a search is completed with a {@link List} of results.
     *
     * @param results the search results from the query.
     */
    void onSearchComplete(List<ArtistItem> results);
  }

  /**
   * Authenticates by client credentials to provide access to Spotify APIs.
   *
   * @param authListener Called when the authentication is complete.
   */
  private static void authenticate(AuthListener authListener) {
    // HTTP Client
    OkHttpClient client = new OkHttpClient();

    // Authentication url
    String url = "https://accounts.spotify.com/api/token";

    // Create basic auth with clientId and clientSecret
    String basicAuthorization = Base64.encodeToString(
        (CLIENT_ID + ":" + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8),
        Base64.NO_WRAP
    );

    // POST Body
    RequestBody formBody = new FormBody.Builder()
        // Defines authentication type
        .add("grant_type", "client_credentials")
        .build();

    // POST Request
    Request request = new Builder()
        .url(url)
        .post(formBody)
        // Basic Authorization used to get auth token
        .addHeader("Authorization", "Basic " + basicAuthorization)
        .build();

    // Send POST
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Log.e(TAG, "onFailure: Error authenticating", e);
        authListener.onAuthenticated(false);
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (response.body() != null) {
          try {
            JSONObject json = new JSONObject(response.body().string());
            sToken = "Bearer " + json.getString("access_token");
            authListener.onAuthenticated(true);
          } catch (JSONException e) {
            Log.e(TAG, "onResponse: Json exception while authenticating", e);
          }
        }
      }
    });

  }

  /**
   * Performs search for artists names matching query. Returns result to searchListener
   *
   * @param query          The artist to search for.
   * @param searchListener Callback for completion of search.
   */
  public static void search(String query, SearchListener searchListener) {
    // Check if authentication is required
    if (sToken == null || sToken.isEmpty()) {
      // Authenticate before search
      authenticate((isAuthenticated) -> {
        // If authenticated perform search
        if (isAuthenticated) {
          search(query, searchListener);
        }
      });
      return;
    }

    // HTTP Client
    OkHttpClient client = new OkHttpClient();

    // Generate GET url
    String formattedQuery = query.replaceAll(" ", "%20");
    String url = String.format(
        "https://api.spotify.com/v1/search?q=%s&type=artist",
        formattedQuery
    );

    // GET Request
    Request request = new Builder()
        .url(url)
        .addHeader("Authorization", sToken)
        .addHeader("Content-Type", "application/json")
        .build();

    // Send call
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        // Log error and return empty results
        Log.e(TAG, "onFailure: Error loading results", e);
        searchListener.onSearchComplete(new ArrayList<>());
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        // Verify response body
        if (response.body() != null) {
          try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            List<ArtistItem> results = new ArrayList<>();
            JSONArray artists = jsonObject.getJSONObject("artists").getJSONArray("items");

            // Populate results
            for (int i = 0; i < artists.length(); i++) {
              // Scoped try/catch will skip over bad json and continue loop
              try {
                results.add(ArtistItem.fromJson(artists.getJSONObject(i)));
              } catch (JSONException e) {
                // Log issue
                Log.e(TAG, "onResponse: JSON Exception while creating ArtistItem", e);
              }
            }

            // Return results
            searchListener.onSearchComplete(results);

          } catch (JSONException e) {
            e.printStackTrace();
          }
        } else {
          // No response, return empty results
          searchListener.onSearchComplete(new ArrayList<>());
        }
      }
    });

  }
}
