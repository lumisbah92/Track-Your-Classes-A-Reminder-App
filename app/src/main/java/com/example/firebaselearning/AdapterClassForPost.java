package com.example.firebaselearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterClassForPost extends RecyclerView.Adapter<AdapterClassForPost.MyViewHolder>{
    private Context context;
    private ArrayList<PostingModel> list;

    public AdapterClassForPost(Context context, ArrayList<PostingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterClassForPost.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_for_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassForPost.MyViewHolder holder, int position) {
        PostingModel postingModel = list.get(position);
        holder.Post.setText(postingModel.getPost());
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.more_menu_item, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Post;
        ImageView moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Post = itemView.findViewById(R.id.PostCardID);
            moreBtn = itemView.findViewById(R.id.MoreID);
        }
    }
}
