package fr.wcs.teamwinsoft.winnews;

import android.content.Context;
import android.net.UrlQuerySanitizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private ArrayList<VideoModel> models;
    private Context context;

    public VideoAdapter(ArrayList<VideoModel> models) {
        this.models = models;
    }

    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.ViewHolder holder, int position) {
        VideoModel videoModel = models.get(position);
        holder.video.setVideoPath(videoModel.getVideo());
        holder.title.setText(videoModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoView video;
        TextView title;

        public ViewHolder(View v) {
            super(v);
            this.title = v.findViewById(R.id.title);
            this.video = v.findViewById(R.id.video_view);
        }
    }
}