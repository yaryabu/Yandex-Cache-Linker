package ru.yaryabu.yandex_cache_linker.logic;

import android.app.Activity;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yaryabu.yandex_cache_linker.model.Artist;


/**
 * Класс для получения Артистов из базы данных или из интернета.
 * При запросе артистов из интернета они сохраняются в базу данных.
 */
public class ArtistsQuery {

    private static final String TAG = ArtistsQuery.class.getSimpleName();

    private static final String ARTISTS_URL
            = "http://cache-default01f.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    public static ArrayList<Artist> getCached() {
        Realm realm = SharedRealm.uiThreadRealm;

        RealmResults<Artist> artistsResults = realm.allObjects(Artist.class);
        ArrayList<Artist> artists = new ArrayList<>();
        artists.addAll(artistsResults);

        Log.v(TAG + ".getCached", artists.toString());

        return artists;

    }

    public static void getNew(final Activity context, final ArtistQueryCallback callback) {

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(ARTISTS_URL)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(Error.networkConnectionError);
                    }
                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String jsonString = response.body().string();
                    final ArrayList<Artist> artists = Artist.artistsForJsonString(jsonString);

                    //FIXME: нужно также чистить из realm старых Artists
                    final Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(artists);
                    realm.commitTransaction();
                    realm.close();

                    Log.v(TAG + ".getNew", artists.toString());

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(artists);
                        }
                    });

                }
                catch (Exception e) {

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(Error.unknownError);
                        }
                    });

                    e.printStackTrace();
                }
            }
        });
    }

}
