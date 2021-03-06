package ru.yaryabu.yandex_cache_linker.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import ru.yaryabu.yandex_cache_linker.R;
import ru.yaryabu.yandex_cache_linker.logic.SharedRealm;
import ru.yaryabu.yandex_cache_linker.model.Artist;

public class ArtistDetailActivity extends AppCompatActivity {

    private Artist mArtist;
    private static final int IMAGE_HEIGHT_PX = (int) (230 * Resources.getSystem().getDisplayMetrics().density);

    @Bind(R.id.artistDetailImageView) ImageView mArtistImageView;
    @Bind(R.id.artistDetailGenresLabel) TextView mGenresLabel;
    @Bind(R.id.artistDetailAlbumsAndTracksLabel) TextView mAlbumsAndTracksLabel;
    @Bind(R.id.artistDetailDescriptionLabel) TextView mDescriptionLabel;
    @Bind(R.id.artistDetailLinkButton) Button mLinkButton;
    @Bind(R.id.artistDetailTopLinkSeparator) View mTopLinkSeparator;
    @Bind(R.id.artistDetailBottomLinkSeparator) View mBottomLinkSeparator;

    @Bind(R.id.artistDetailYandexMusicFab) FloatingActionButton mYandexMusicFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        ButterKnife.bind(this);

        long artistId = getIntent().getLongExtra(ArtistAdapter.ARTIST_EXTRA, -1);
        Realm realm = SharedRealm.uiThreadRealm;
        mArtist = realm.where(Artist.class).equalTo("mId", artistId).findFirst();
        setTitle(mArtist.getName());

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;


        Picasso.with(this)
                .load(mArtist.getBigCoverUrlString())
                .resize(width, IMAGE_HEIGHT_PX)
                .centerCrop()
                .into(mArtistImageView);
        mGenresLabel.setText(mArtist.getGenresFormattedString());

        String albumsAndTracks =  String.format(
                getString(R.string.artistDetailAlbumsAndTracksTemplate),
                mArtist.getAlbumsCount(),
                mArtist.getTracksCount()
        );
        mAlbumsAndTracksLabel.setText(albumsAndTracks);
        mDescriptionLabel.setText(mArtist.getDescription());
        if (mArtist.getLink() != null) {
            mLinkButton.setText(mArtist.getLink());
        } else {
            mLinkButton.setVisibility(View.GONE);
            mTopLinkSeparator.setVisibility(View.GONE);
            mBottomLinkSeparator.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.artistDetailLinkButton)
    public void linkButtonClicked() {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.getLink()));
            startActivity(browserIntent);
    }

    @OnClick(R.id.artistDetailYandexMusicFab)
    public void yandexMusicFabClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.getYandexMusicRedirectLink()));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
