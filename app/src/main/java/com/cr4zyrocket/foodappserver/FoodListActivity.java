package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Category;
import com.cr4zyrocket.foodappserver.Model.Food;
import com.cr4zyrocket.foodappserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FoodListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerFood;
    RecyclerView.LayoutManager layoutManager;
    String categoryID="";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    SwipeRefreshLayout srlFoodList;
    LayoutAnimationController controller;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
        setContentView(R.layout.activity_food_list);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        setTitle("Food list");

        //Get Intent
        if (getIntent()!=null){
            categoryID=getIntent().getStringExtra("CategoryID");
        }


        //Init Firebase
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Food").child(Common.currentLanguage);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("categoryID").equalTo(categoryID), Food.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                holder.tvFoodName.setText(model.getName());
                Locale locale=new Locale("vi","VN");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                holder.tvFoodPrice.setText(fmt.format(Integer.parseInt(model.getPrice())));
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.ivFoodImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.ivFoodImage.setImageResource(R.drawable.image_notfound);
                    }
                });


                holder.setItemClickListener((view, position1, isLongClick) -> {
                    //Start new Activity
                    Intent foodDetailIntent=new Intent(FoodListActivity.this,FoodDetailActivity.class);
                    foodDetailIntent.putExtra("FoodID",adapter.getRef(position1).getKey());
                    startActivity(foodDetailIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
                holder.itemView.setOnLongClickListener(v -> {
                    setPosition(Integer.parseInt(Objects.requireNonNull(adapter.getRef(holder.getAdapterPosition()).getKey())));
                    return false;
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }

        };

        //Load food
        recyclerFood=findViewById(R.id.recyclerFood);
        recyclerFood.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        controller= AnimationUtils.loadLayoutAnimation(recyclerFood.getContext(),R.anim.layout_from_right);
        recyclerFood.setLayoutAnimation(controller);
        recyclerFood.setLayoutManager(layoutManager);
        recyclerFood.startAnimation(controller.getAnimation());

        //View
        srlFoodList=findViewById(R.id.srlFoodList);
        srlFoodList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        srlFoodList.setOnRefreshListener(() -> {
            if (categoryID!=null&&categoryID.length()>0){
                if (Common.isConnectedToInternet(getBaseContext())) {
                    srlFoodList.setRefreshing(true);
                    loadFoodList();
                }else {
                    Toast.makeText(FoodListActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        srlFoodList.post(() -> {
            if (categoryID!=null&&categoryID.length()>0){
                if (Common.isConnectedToInternet(getBaseContext())) {
                    srlFoodList.setRefreshing(true);
                    loadFoodList();
                }else {
                    Toast.makeText(FoodListActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.addFoodMenu){
            Intent intent=new Intent(FoodListActivity.this,AddFoodInfoActivity.class);
            intent.putExtra("categoryID",categoryID);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerFood.startAnimation(controller.getAnimation());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadFoodList() {
        adapter.startListening();
        recyclerFood.setAdapter(adapter);
        srlFoodList.setRefreshing(false);
        recyclerFood.scheduleLayoutAnimation();
        registerForContextMenu(recyclerFood);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int pos = -1;
        try {
            pos = getPosition();
        } catch (Exception e) {
            Log.d("homeAc", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        CharSequence title = item.getTitle();
        if (title.equals(Common.EDIT)){
            Intent intent=new Intent(FoodListActivity.this,EditFoodInfoActivity.class);
            intent.putExtra("foodID",pos+"");
            intent.putExtra("categoryID",categoryID);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if(title.equals(Common.DELETE)){
            deleteFood(pos);
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(int foodID) {
        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("Delete food")
                .setMessage("Are you sure to delete this food ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    database.getReference("Food").child("en").orderByKey().equalTo(foodID+"").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                    database.getReference("Food").child("vi").orderByKey().equalTo(foodID+"").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                    database.getReference("Rating").orderByChild("foodID").equalTo(foodID+"").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(this, "Delete food successfully !", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {

                })
                .create();
        alertDialog.show();
    }

}