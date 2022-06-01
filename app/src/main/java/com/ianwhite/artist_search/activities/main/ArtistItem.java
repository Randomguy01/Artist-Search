package com.ianwhite.artist_search.activities.main;

import com.ianwhite.artist_search.R;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an {@link R.layout#artist_item}, storing the artists name, number of followers, and a
 * link to the artist's profile photo.
 */
public class ArtistItem implements Serializable {
  private static final long serialVersionUID = -8580369514192409515L;

  /**
   * The artist's name.
   */
  private final String mName;

  /**
   * Url to the artist's profile image.
   */
  private final String mImageUrl;

  /**
   * The number of people following the artist.
   */
  private final double mFollowers;

  /**
   * Creates an {@link ArtistItem} with all fields.
   */
  public ArtistItem(String name, String imageUrl, double followers) {
    mName = name;
    mImageUrl = imageUrl;
    mFollowers = followers;
  }

  /**
   * Creates an {@link ArtistItem} from a {@link JSONObject}.
   *
   * @param jsonObject used to create the {@link ArtistItem}.
   *
   * @return {@link ArtistItem}.
   * @throws JSONException If an invalid JSON object is read.
   */
  public static ArtistItem fromJson(JSONObject jsonObject) throws JSONException {
    // Get smallest image url
    String imageUrl = null;
    JSONArray images = jsonObject.getJSONArray("images");
    if (images.length() > 0) {
      imageUrl = images.getJSONObject(images.length() - 1).getString("url");
    }

    // Create ArtistItem
    return new ArtistItem(
        jsonObject.getString("name"),
        imageUrl,
        jsonObject.getJSONObject("followers").getDouble("total")
    );
  }

  public String getName() {
    return mName;
  }

  public String getImageUrl() {
    return mImageUrl;
  }

  public double getFollowers() {
    return mFollowers;
  }

}
