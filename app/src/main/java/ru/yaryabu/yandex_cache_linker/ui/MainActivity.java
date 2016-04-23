package ru.yaryabu.yandex_cache_linker.ui;

import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
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
    @Bind(R.id.artistListSwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    private ArtistAdapter mAdapter;

    private static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(R.string.mainActivityTitle);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        configureRealmOnLaunch();
        configureRecycleViewOnLaunch();

        ArrayList<Artist> artists = ArtistsQuery.getCached();
        mAdapter = new ArtistAdapter(MainActivity.this, artists);
        mRecyclerView.setAdapter(mAdapter);

        refresh();
    }

    private void refresh() {
        ArtistsQuery.getNew(this, new ArtistQueryCallback() {
            @Override
            public void onFailure(Error error) {
                handleError(error);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(ArrayList<Artist> artists) {
                mAdapter.swap(artists);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void handleError(Error error) {
        String errorText;
        switch (error) {
            case networkConnectionError:
                errorText = "Ошибка соединения с сервером";
                break;
            default:
                errorText = "Неизвестная ошибка";
                break;
        }
        System.out.println("ERROR " + errorText + " " + error.toString());
        Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
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

    // методы ниже отвечают за сохранение позиции RecyclerView при возвращении с другой Activity

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }
    }
}
