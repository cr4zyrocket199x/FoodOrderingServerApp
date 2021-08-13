package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class EditFoodInfoActivity extends AppCompatActivity {
    Button btnCancelEditFood,btnEditFood;
    EditText edtEditFoodNameEn,edtEditFoodNameVi,edtEditFoodImage,edtEditFoodPrice,edtEditFoodDescriptionEn,edtEditFoodDescriptionVi;
    FirebaseDatabase database;
    String currentFoodID="";
    String currentCategoryID="";

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
        setContentView(R.layout.activity_edit_food_info);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        setTitle("Edit food");

        database=FirebaseDatabase.getInstance();
        btnCancelEditFood=findViewById(R.id.btnCancelEditFood);
        btnEditFood=findViewById(R.id.btnEditFood);
        edtEditFoodNameEn=findViewById(R.id.edtEditFoodNameEn);
        edtEditFoodNameVi=findViewById(R.id.edtEditFoodNameVi);
        edtEditFoodImage=findViewById(R.id.edtEditFoodImage);
        edtEditFoodPrice=findViewById(R.id.edtEditFoodPrice);
        edtEditFoodDescriptionEn=findViewById(R.id.edtEditFoodDescriptionEn);
        edtEditFoodDescriptionVi=findViewById(R.id.edtEditFoodDescriptionVi);

        //Get Intent
        if (getIntent()!=null){
            currentFoodID=getIntent().getStringExtra("foodID");
            currentCategoryID=getIntent().getStringExtra("categoryID");
        }
        loadFoodInfo();
        btnEditFood.setOnClickListener(v -> {
            if (edtEditFoodNameEn.getText().toString().trim().length()>0
                    && edtEditFoodNameVi.getText().toString().trim().length()>0
                    && edtEditFoodImage.getText().toString().trim().length()>0
                    && edtEditFoodPrice.getText().toString().trim().length()>0
                    && edtEditFoodDescriptionEn.getText().toString().trim().length()>0
                    && edtEditFoodDescriptionVi.getText().toString().trim().length()>0) {
                Food foodEn=new Food(edtEditFoodNameEn.getText().toString(),
                        edtEditFoodImage.getText().toString(),
                        edtEditFoodDescriptionEn.getText().toString(),
                        edtEditFoodPrice.getText().toString(),"0",
                        currentCategoryID);
                Food foodVi=new Food(edtEditFoodNameVi.getText().toString(),
                        edtEditFoodImage.getText().toString(),
                        edtEditFoodDescriptionVi.getText().toString(),
                        edtEditFoodPrice.getText().toString(),"0",
                        currentCategoryID);
                database.getReference("Food").child("en").child(currentFoodID + "").setValue(foodEn);
                database.getReference("Food").child("vi").child(currentFoodID + "").setValue(foodVi);
                Toast.makeText(this, "Edit food successfully !", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        btnCancelEditFood.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void loadFoodInfo() {
        database.getReference("Food").child("en").orderByKey().equalTo(currentFoodID+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    edtEditFoodNameEn.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getName());
                    edtEditFoodImage.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getImage());
                    edtEditFoodDescriptionEn.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getDescription());
                    edtEditFoodPrice.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        database.getReference("Food").child("vi").orderByKey().equalTo(currentFoodID+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    edtEditFoodNameVi.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getName());
                    edtEditFoodDescriptionVi.setText(Objects.requireNonNull(dataSnapshot.getValue(Food.class)).getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}