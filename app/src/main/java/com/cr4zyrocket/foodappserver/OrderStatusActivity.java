package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DirectAction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Request;
import com.cr4zyrocket.foodappserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderStatusActivity extends AppCompatActivity {
    MaterialSpinner spinnerOrderStatus;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requestDataRef;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    private String orderID;
    MaterialSearchBar materialSearchBarFood;
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/res_font.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_order_status);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order status");

        //Firebase
        database=FirebaseDatabase.getInstance();
        requestDataRef =database.getReference("Requests");
        recyclerView=findViewById(R.id.recyclerListOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadAllOrders();
        //Search
        materialSearchBarFood=findViewById(R.id.searchBarOrder);
        materialSearchBarFood.setCardViewElevation(10);
        materialSearchBarFood.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When searchBar is close
                //Restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearchOrder(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }
    private void startSearchOrder(CharSequence phone){
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requestDataRef.orderByChild("phone").equalTo(phone.toString()), Request.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position, @NonNull final Request model) {
                String orID= "Order ID: "+adapter.getRef(position).getKey();
                String orS= "Order status: "+ Common.convertCodeToStatus(model.getStatus());
                String orA="Order address: "+model.getAddress();
                String orP="Order phone: "+model.getPhone();
                String orC="Order comment: "+model.getComment();
                holder.tvOrderID.setText(orID);
                holder.tvOrderStatus.setText(orS);
                holder.tvOrderAddress.setText(orA);
                holder.tvOrderPhone.setText(orP);
                holder.tvOrderComment.setText(orC);

                Common.currentRequest=model;
                holder.itemView.setOnLongClickListener(v -> {
                    setOrderID(adapter.getRef(holder.getAdapterPosition()).getKey());
                    return false;
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
    }
    private void loadAllOrders() {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requestDataRef.orderByPriority(), Request.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Request model) {
                String orID= "Order ID: "+adapter.getRef(position).getKey();
                String orS= "Order status: "+ Common.convertCodeToStatus(model.getStatus());
                String orA="Order address: "+model.getAddress();
                String orP="Order phone: "+model.getPhone();
                String orC="Order comment: "+model.getComment();
                holder.tvOrderID.setText(orID);
                holder.tvOrderStatus.setText(orS);
                holder.tvOrderAddress.setText(orA);
                holder.tvOrderPhone.setText(orP);
                holder.tvOrderComment.setText(orC);

                Common.currentRequest=model;
                holder.itemView.setOnLongClickListener(v -> {
                    setOrderID(adapter.getRef(holder.getAdapterPosition()).getKey());
                    return false;
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String orderID;
        try {
            orderID = getOrderID();
        } catch (Exception e) {
            Log.d("OrderStatus", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        CharSequence title = item.getTitle();
        if (title.equals(Common.UPDATE)){
            updateOrder(orderID);
        }else if(title.equals(Common.DELETE)){
            deleteOrder(orderID);
        }
        else if(title.equals(Common.SHOW_DETAILS)){
            showAllDetails(orderID);
        }
        return super.onContextItemSelected(item);
    }

    private void showAllDetails(String orderID) {
        Intent orderDetailIntent=new Intent(OrderStatusActivity.this,OrderDetailsActivity.class);
        orderDetailIntent.putExtra("OrderID",orderID);
        startActivity(orderDetailIntent);
    }

    private void deleteOrder(String orderID) {
        requestDataRef.child(orderID).removeValue();
    }

    private void updateOrder(String orderID) {
        View v=this.getLayoutInflater().inflate(R.layout.update_order_layout,null);
        spinnerOrderStatus=v.findViewById(R.id.spinnerOrderStatus);
        spinnerOrderStatus.setItems("Placed","Processing","On my way","Shipped","Cancel");
        AlertDialog alertDialog=new AlertDialog.Builder(OrderStatusActivity.this)
                .setTitle("Update order status")
                .setMessage("Please choose status :")
                .setView(v)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Common.currentRequest.setStatus(String.valueOf(spinnerOrderStatus.getSelectedIndex()));
                    requestDataRef.child(orderID).setValue(Common.currentRequest);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .create();
        alertDialog.show();
    }
}