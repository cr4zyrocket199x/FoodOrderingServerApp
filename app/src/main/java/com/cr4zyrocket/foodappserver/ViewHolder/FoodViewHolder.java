package com.cr4zyrocket.foodappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Interface.ItemClickListener;
import com.cr4zyrocket.foodappserver.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tvFoodName,tvFoodPrice;
    public ImageView ivFoodImage;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        tvFoodName=itemView.findViewById(R.id.tvFoodName);
        tvFoodPrice=itemView.findViewById(R.id.tvFoodPrice);
        ivFoodImage=itemView.findViewById(R.id.ivFoodImage);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, Common.EDIT);
        menu.add(0, v.getId(), 0, Common.DELETE);
    }
}
