package com.pingala.marathonphotos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Habeeb on 2/7/2017.
 */

public class StaggeredGridLayoutAdapter extends CustomRecyclerViewAdapter {
    private Activity activity;
    private ArrayList<Images> images;
    private int screenWidth;
    private int screenHeight;

    public StaggeredGridLayoutAdapter(Activity activity, ArrayList<Images> images) {
        this.activity = activity;
        this.images = images;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.x;
    }

    @Override
    public CustomRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.row_grid_item, parent, false);
        Holder dataObjectHolder = new Holder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final CustomRecycleViewHolder holder, final int position) {
        Log.e("fullImageurl",""+images.get(position).getImgUrl());
        final Holder myHolder = (Holder) holder;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(images.get(position).getImg(), opts);
        opts.inJustDecodeBounds = false;
        Picasso.with(activity)
                .load(images.get(position).getImg())
                .error( R.drawable.ic_error )
                .placeholder( R.drawable.progress_animation )
                .resize(screenWidth / 2, screenWidth /2)
                .centerCrop()
                .into((myHolder.images));
           ((Holder) holder).images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, WallpaperActivity.class);
                i.putExtra("fullImage",images.get(position).getImgUrl());
                i.putExtra("imageName","image"+position);
                activity.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class Holder extends CustomRecycleViewHolder {
        private ImageView images;

        public Holder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.ivItemGridImage);
        }
    }
}
