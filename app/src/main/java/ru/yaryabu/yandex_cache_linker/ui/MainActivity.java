package ru.yaryabu.yandex_cache_linker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.yaryabu.yandex_cache_linker.R;
import ru.yaryabu.yandex_cache_linker.logic.ArtistQueryCallback;
import ru.yaryabu.yandex_cache_linker.logic.ArtistsQuery;
import ru.yaryabu.yandex_cache_linker.logic.Error;
import ru.yaryabu.yandex_cache_linker.logic.SharedRealm;
import ru.yaryabu.yandex_cache_linker.model.Artist;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.artistsRecyclerView) RecyclerView mRecyclerView;
    private ArtistAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configureRealmOnLaunch();
        configureRecycleViewOnLaunch();

        ArrayList<Artist> artists = ArtistsQuery.getCached();
        mAdapter = new ArtistAdapter(MainActivity.this, artists);
        mRecyclerView.setAdapter(mAdapter);

        ArtistsQuery.getNew(this, new ArtistQueryCallback() {
            @Override
            public void onFailure(Error error) {
                handleError(error);
            }

            @Override
            public void onResponse(ArrayList<Artist> artists) {
                System.out.println("NET");
                mAdapter.swap(artists);
            }
        });

        System.out.println("END");
    }

    private void handleError(Error error) {
        //FIXME: обработка ошибок
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    private void configureRealmOnLaunch() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        SharedRealm.uiThreadRealm = Realm.getDefaultInstance();
    }

    private void configureRecycleViewOnLaunch() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }
}
