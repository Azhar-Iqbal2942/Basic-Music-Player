package com.example.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    ArrayList<Song> mSongs;
    private OnItemClickListener mListener;
    private static int viewHolderCount;
    private int mNumberItems;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SongAdapter(ArrayList<Song> songs) {
        mSongs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new SongViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        Song currentSong = mSongs.get(position);

        songViewHolder.mTitle.setText(currentSong.getTitle());
        songViewHolder.mArtist.setText(currentSong.getArtist());

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public TextView mArtist;
        public ImageView mThumbnail;

        public SongViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mArtist = (TextView) itemView.findViewById(R.id.artist);
            mThumbnail = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }
            });

        }
    }
}
