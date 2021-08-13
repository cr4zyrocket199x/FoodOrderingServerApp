package com.cr4zyrocket.foodappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Interface.ItemClickListener;
import com.cr4zyrocket.foodappserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView tvOrderID,tvOrderStatus,tvOrderPhone,tvOrderAddress,tvOrderComment;
    public ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOrderAddress=itemView.findViewById(R.id.tvOrderAddress);
        tvOrderID=itemView.findViewById(R.id.tvOrderID);
        tvOrderStatus=itemView.findViewById(R.id.tvOrderStatus);
        tvOrderPhone=itemView.findViewById(R.id.tvOrderPhone);
        tvOrderComment=itemView.findViewById(R.id.tvOrderComment);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, Common.UPDATE);
        menu.add(0, v.getId(), 0, Common.DELETE);
        menu.add(0, v.getId(), 0, Common.SHOW_DETAILS);
    }
}
