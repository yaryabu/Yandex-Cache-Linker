package ru.yaryabu.yandex_cache_linker.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.yaryabu.yandex_cache_linker.R;
import ru.yaryabu.yandex_cache_linker.model.Artist;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context mContext;
    private ArrayList<Artist> mArtists;

    public ArtistAdapter(Context context, ArrayList<Artist> artists) {
        mContext = context;
        mArtists = artists;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_list_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        holder.bindArtist(mArtists.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public void swap(ArrayList<Artist> artists){
        mArtists.clear();
        mArtists.addAll(artists);
        notifyDataSetChanged();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder
                                    implements View.OnClickListener {

        public ImageView artistImageView;
        public TextView artistNameLabel;
        public TextView artistGenresLabel;
        public TextView artistTracksAndAlbumsLabel;
        private String mYandexMusicRedirectUrl;

        public ArtistViewHolder(View itemView) {
            super(itemView);

            artistImageView = (ImageView) itemView.findViewById(R.id.artistImageView);
            artistNameLabel = (TextView) itemView.findViewById(R.id.artistNameLabel);
            artistGenresLabel = (TextView) itemView.findViewById(R.id.artistGenresLabel);
            artistTracksAndAlbumsLabel = (TextView) itemView.findViewById(R.id.artistTracksAndAlbumsLabel);

            itemView.setOnClickListener(this);
        }

        public void bindArtist(Artist artist) {
            Picasso.with(mContext)
                    .load(artist.getSmallCoverUrlString())
                    .into(artistImageView);

            artistNameLabel.setText(artist.getName());

            artistGenresLabel.setText(artist.getGenresFormattedString());
            String tracksAndAlbumsFormattedString =
                    String.format(mContext.getString(R.string.artistsListTracksAndAlbumsTemplate),
                            artist.getAlbumsCount(),
                            artist.getTracksCount()
                    );
            artistTracksAndAlbumsLabel.setText(tracksAndAlbumsFormattedString);

            mYandexMusicRedirectUrl = artist.getYandexMusicRedirectLink();
        }

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mYandexMusicRedirectUrl));
            mContext.startActivity(browserIntent);
        }
    }

}
