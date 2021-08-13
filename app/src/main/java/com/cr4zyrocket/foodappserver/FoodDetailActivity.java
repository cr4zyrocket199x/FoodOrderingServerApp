package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Food;
import com.cr4zyrocket.foodappserver.Model.Rating;
import com.cr4zyrocket.foodappserver.ViewHolder.CommentAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FoodDetailActivity extends AppCompatActivity {

    RecyclerView rvComment;
    RecyclerView.LayoutManager layoutManager;
    CommentAdapter adapter;
    List<Rating> ratingList;
    TextView tvFoodName,tvFoodPrice,tvFoodDescription;
    ImageView ivFoodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String foodID="",categoryID;
    FirebaseDatabase database;
    DatabaseReference food,ratingData;
    Food currentFood;
    RatingBar ratingBar;
    float sum,count;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        setContentView(R.layout.activity_food_detail);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        setTitle("Food detail");

        //Get Intent
        if (getIntent()!=null){
            categoryID=getIntent().getStringExtra("CategoryID");
        }

        //Firebase
        database=FirebaseDatabase.getInstance();
        food=database.getReference("Food").child(Common.currentLanguage);
        ratingData=database.getReference("Rating");

        //Init view
        rvComment=findViewById(R.id.rvListComment);
        rvComment.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        rvComment.setLayoutManager(layoutManager);
        tvFoodDescription=findViewById(R.id.tvFoodDescription);
        tvFoodName=findViewById(R.id.tvFoodName);
        tvFoodPrice=findViewById(R.id.tvFoodPrice);
        ivFoodImage=findViewById(R.id.iv_Food);
        ratingBar=findViewById(R.id.ratingBar);
        collapsingToolbarLayout=findViewById(R.id.ctlFoodDetail);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get FoodID from Intent
        if(getIntent()!=null){
            foodID=getIntent().getStringExtra("FoodID");
            if (foodID!=null && foodID.length()>0){
                if (Common.isConnectedToInternet(getBaseContext())) {
                    getFoodDetail(foodID);
                    getRatingFood(foodID);
                    showAllComment(foodID);
                }else {
                    Toast.makeText(this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    private void showAllComment(String foodID){
        ratingData.orderByChild("foodID").equalTo(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ratingList=new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ratingList.add(dataSnapshot.getValue(Rating.class));
                }
                adapter=new CommentAdapter(ratingList,getBaseContext());
                rvComment.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getRatingFood(String foodID) {
        com.google.firebase.database.Query foodRating=ratingData.orderByChild("foodID").equalTo(foodID);
        foodRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Rating item=dataSnapshot.getValue(Rating.class);
                    assert item != null;
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFoodDetail(String foodID) {
        food.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood=snapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(ivFoodImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        ivFoodImage.setImageResource(R.drawable.image_notfound);
                    }
                });
                Locale locale=new Locale("vi","VN");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                tvFoodPrice.setText(fmt.format(Integer.parseInt(currentFood.getPrice())));
                tvFoodDescription.setText(currentFood.getDescription());
                tvFoodName.setText(currentFood.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}