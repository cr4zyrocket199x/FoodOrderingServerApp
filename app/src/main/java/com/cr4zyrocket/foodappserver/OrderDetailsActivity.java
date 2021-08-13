package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Request;
import com.cr4zyrocket.foodappserver.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderDetailsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference requestDataRef;
    TextView tvOrderID,tvOrderPhone,tvOrderAddress,tvOrderTotal,tvOrderComment;
    String order_id_value,orID,orP,orA,orT,orC;
    RecyclerView rvListFood;
    RecyclerView.LayoutManager layoutManager;
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
        setContentView(R.layout.activity_order_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order details");

        //Firebase
        database= FirebaseDatabase.getInstance();
        requestDataRef =database.getReference("Requests");

        tvOrderID=findViewById(R.id.tvOrderID);
        tvOrderPhone=findViewById(R.id.tvOrderPhone);
        tvOrderAddress=findViewById(R.id.tvOrderAddress);
        tvOrderTotal=findViewById(R.id.tvOrderTotal);
        tvOrderComment=findViewById(R.id.tvOrderComment);
        rvListFood=findViewById(R.id.rvListFood);
        layoutManager=new LinearLayoutManager(this);
        rvListFood.setLayoutManager(layoutManager);

        if (getIntent()!=null){
            order_id_value=getIntent().getStringExtra("OrderID");
        }

        orID="Order ID: "+order_id_value;
        orP= "Order phone: "+ Common.currentRequest.getPhone();
        orA="Order address: "+Common.currentRequest.getAddress();
        orT="Order Total: "+Common.currentRequest.getTotal();
        orC="Order comment: "+Common.currentRequest.getComment();
        tvOrderID.setText(orID);
        tvOrderPhone.setText(orP);
        tvOrderAddress.setText(orA);
        tvOrderTotal.setText(orT);
        tvOrderComment.setText(orC);

        OrderDetailAdapter adapter=new OrderDetailAdapter(Common.currentRequest.getFoodOrdered());
        adapter.notifyDataSetChanged();
        rvListFood.setAdapter(adapter);
    }
}