package com.ashutosh.ndkpixels;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vostro-Daily on 12/25/2017.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private final int VIEW_TYPE_IMAGE = 1;
    private final int VIEW_TYPE_GRAPH = 2;

    private Bitmap imgBitmap;
    private List<ItemContent> itemContentList;

    public ContentAdapter() {
        itemContentList = new ArrayList<>();
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_IMAGE){
            return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_img, parent, false));
        } else {
            return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_graph, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if(viewType == VIEW_TYPE_IMAGE){
            if(holder.imageView != null && imgBitmap != null){
                holder.imageView.setImageBitmap(imgBitmap);
            }
        } else {
            if(position >= 1){
                // Graph view
                ItemContent itemContent = itemContentList.get(position - 1);
                int [] freq = itemContent.getFreq();

                DataPoint dataPoints [] = new DataPoint[freq.length - 1];

                for (int i = 1; i < freq.length; i++) {
                    dataPoints[i - 1] = new DataPoint(i, freq[i]);
                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                series.setColor(itemContent.getColor());
//                series.setTitle(itemContent.getTitle());

                if (holder.graphView != null) {
                    holder.graphView.removeAllSeries();
                    holder.graphView.addSeries(series);
                    holder.graphView.setTitle(itemContent.getTitle());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        // +1 for image
        if(imgBitmap != null) {
            return itemContentList.size() + 1;
        } else {
            return itemContentList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_IMAGE;
        } else {
            return VIEW_TYPE_GRAPH;
        }
    }

    public void addImage(Bitmap bitmap) {
        this.imgBitmap = bitmap;

        notifyDataSetChanged();
    }

    public void addItem(ItemContent itemContent) {
        this.itemContentList.add(itemContent);
    }

    public void resetData() {
        this.itemContentList.clear();
        this.imgBitmap = null;

        notifyDataSetChanged();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        GraphView graphView;

        ContentViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imgView);
            graphView = (GraphView) itemView.findViewById(R.id.graph);
        }
    }
}
