package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Model.Category;
import com.cr4zyrocket.foodappserver.Model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddFoodInfoActivity extends AppCompatActivity {
    Button btnAddFood,btnCancelAddFood;
    EditText edtFoodNameEn,edtFoodNameVi,edtFoodImage,edtFoodPrice,edtFoodDescriptionEn,edtFoodDescriptionVi;
    FirebaseDatabase database;
    DatabaseReference categories;
    ArrayList<String> categoryList;
    String currentCategoryID="",cateIDIntent="";

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
        setContentView(R.layout.activity_add_food_info);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        setTitle("Add new food");

        database=FirebaseDatabase.getInstance();
        categories=database.getReference("Category");
        btnAddFood=findViewById(R.id.btnAddFood);
        btnCancelAddFood=findViewById(R.id.btnCancelAddFood);
        edtFoodNameEn=findViewById(R.id.edtFoodNameEn);
        edtFoodNameVi=findViewById(R.id.edtFoodNameVi);
        edtFoodImage=findViewById(R.id.edtFoodImage);
        edtFoodPrice=findViewById(R.id.edtFoodPrice);
        edtFoodDescriptionEn=findViewById(R.id.edtFoodDescriptionEn);
        edtFoodDescriptionVi=findViewById(R.id.edtFoodDescriptionVi);
        categoryList=new ArrayList<>();



        //Get Intent
        if (getIntent()!=null) {
            cateIDIntent = getIntent().getStringExtra("categoryID");
        }
        btnAddFood.setOnClickListener(v -> {
            if (edtFoodNameEn.getText().toString().trim().length()>0
                    &&edtFoodNameVi.getText().toString().trim().length()>0
                    && edtFoodImage.getText().toString().trim().length()>0
                    && edtFoodPrice.getText().toString().trim().length()>0
                    && edtFoodDescriptionEn.getText().toString().trim().length()>0
                    && edtFoodDescriptionVi.getText().toString().trim().length()>0){
                DatabaseReference foodDataRef = FirebaseDatabase.getInstance().getReference("Food");
                foodDataRef.child("en").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            int lastKey =Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                            Food foodEn = new Food(edtFoodNameEn.getText().toString(),
                                    edtFoodImage.getText().toString(),
                                    edtFoodDescriptionEn.getText().toString(),
                                    edtFoodPrice.getText().toString(), "0", cateIDIntent);
                            foodDataRef.child("en").child(String.valueOf(lastKey+1)).setValue(foodEn);
                            Toast.makeText(AddFoodInfoActivity.this, "Add new food successfully !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                foodDataRef.child("vi").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            int lastKey =Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                            Food foodVi = new Food(edtFoodNameVi.getText().toString(),
                                    edtFoodImage.getText().toString(),
                                    edtFoodDescriptionVi.getText().toString(),
                                    edtFoodPrice.getText().toString(), "0", cateIDIntent);
                            foodDataRef.child("vi").child(String.valueOf(lastKey+1)).setValue(foodVi);
                            Toast.makeText(AddFoodInfoActivity.this, "Add new food successfully !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                finish();
            }else {
                Toast.makeText(this, "Please fill all information of food !", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancelAddFood.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
}