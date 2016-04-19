package ru.yaryabu.yandex_cache_linker.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Artist extends RealmObject {
    @PrimaryKey
    private long mId;
    private String mName;
    // во всем МП не меняется формат строки жанров, так что можем позволить сразу форматировать массив жанров.
    // также в Realm не подерживается массив строк и нужно использовать дураций workaround http://stackoverflow.com/questions/28128958/android-realm-storing-int-and-string
    private String mGenresFormattedString;
    private int mTracksCount;
    private int mAlbumsCount;
    private String mDescription;
    private String mSmallCoverUrlString;
    private String mBigCoverUrlString;
    private String mLink;

    public static ArrayList<Artist> artistsForJsonString(String jsonString) throws JSONException {
        JSONArray jArray = new JSONArray(jsonString);
        final ArrayList<Artist> artists = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObj = jArray.getJSONObject(i);
            Artist artist = new Artist();
            artist.setId(jObj.getLong("id"));
            artist.setName(jObj.getString("name"));

            JSONArray genresArray = jObj.getJSONArray("genres");

            String genresFormattedString = "";
            for (int x = 0; x < genresArray.length(); x++) {
                String genreFormat = genresArray.getString(x) + ", ";

                genresFormattedString += genreFormat;
            }
            if (genresArray.length() > 0) {
                genresFormattedString = genresFormattedString.substring(0, genresFormattedString.length() - 2);
            }

            artist.setGenresFormattedString(genresFormattedString);

            artist.setTracksCount(jObj.getInt("tracks"));
            artist.setAlbumsCount(jObj.getInt("albums"));
            artist.setDescription(jObj.getString("description"));
            artist.setSmallCoverUrlString(jObj.getJSONObject("cover").getString("small"));
            artist.setBigCoverUrlString(jObj.getJSONObject("cover").getString("big"));
            // link - необязательный параметр. Есть не у всех объектов.
            try {
                artist.setLink(jObj.getString("link"));
            } catch (JSONException e) {
                artist.setLink(null);
            }

            artists.add(artist);
        }

        return artists;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getGenresFormattedString() {
        return mGenresFormattedString;
    }

    public void setGenresFormattedString(String genresFormattedString) {
        mGenresFormattedString = genresFormattedString;
    }

    public int getTracksCount() {
        return mTracksCount;
    }

    public void setTracksCount(int tracksCount) {
        mTracksCount = tracksCount;
    }

    public int getAlbumsCount() {
        return mAlbumsCount;
    }

    public void setAlbumsCount(int albumsCount) {
        mAlbumsCount = albumsCount;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getSmallCoverUrlString() {
        return mSmallCoverUrlString;
    }

    public void setSmallCoverUrlString(String smallCoverUrlString) {
        mSmallCoverUrlString = smallCoverUrlString;
    }

    public String getBigCoverUrlString() {
        return mBigCoverUrlString;
    }

    public void setBigCoverUrlString(String bigCoverUrlString) {
        mBigCoverUrlString = bigCoverUrlString;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getYandexMusicRedirectLink() {
        return "https://music.yandex.ru/artist/" + getId();
    }
}
