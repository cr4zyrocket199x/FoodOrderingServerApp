package com.cr4zyrocket.foodappserver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Banner;
import com.cr4zyrocket.foodappserver.Model.Category;
import com.cr4zyrocket.foodappserver.ViewHolder.MenuViewHolder;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    SwipeRefreshLayout srlHome;
    TextView tvFullName;
    ImageView ivProfile;
    LayoutAnimationController controller;
    RecyclerView recyclerMenu;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference category;
    SliderLayout sliderLayout;
    HashMap<String,String> image_list;
    FloatingActionButton fabAddCategory;
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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for user
        View headerView=navigationView.getHeaderView(0);
        tvFullName=headerView.findViewById(R.id.txtFullName);
        ivProfile=headerView.findViewById(R.id.ivMenuIcon);

        //Set information
        tvFullName.setText(Common.getCurrentManagerName());
        ivProfile.setImageResource(R.drawable.logo_eat_it);

        //View
        srlHome=findViewById(R.id.srlHome);
        srlHome.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        srlHome.setOnRefreshListener(() -> {
            if (Common.isConnectedToInternet(getBaseContext())){
                loadMenu();
            }else {
                Toast.makeText(HomeActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
            }
        });

        //Add category
        fabAddCategory=findViewById(R.id.fab);
        fabAddCategory.setOnClickListener(v -> {
            showDialogAddCategory();
        });

        //Load menu
        recyclerMenu=findViewById(R.id.recycler_menu);
        controller= AnimationUtils.loadLayoutAnimation(recyclerMenu.getContext(),R.anim.layout_fall_down);
        recyclerMenu.setLayoutAnimation(controller);
        recyclerMenu.setLayoutManager(new GridLayoutManager(this,2));
        recyclerMenu.startAnimation(controller.getAnimation());

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category").child(Common.currentLanguage);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder holder, int position, @NonNull Category model) {
                holder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.imageView.setImageResource(R.drawable.image_notfound);
                    }
                });
                holder.setItemClickListener((view, position1, isLongClick) -> {
                    //Get CategoryID and send to new Activity
                    Intent foodListIntent=new Intent(HomeActivity.this, FoodListActivity.class);
                    foodListIntent.putExtra("CategoryID",adapter.getRef(position1).getKey());
                    startActivity(foodListIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
                holder.itemView.setOnLongClickListener(v -> {
                    setPosition(Integer.parseInt(Objects.requireNonNull(adapter.getRef(holder.getAdapterPosition()).getKey())));
                    return false;
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };

        srlHome.setOnRefreshListener(() -> {
            if (Common.isConnectedToInternet(getBaseContext())){
                loadMenu();
            }else {
                Toast.makeText(HomeActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
            }
        });

        //Default, load for first item
        srlHome.post(() -> {
            if (Common.isConnectedToInternet(getBaseContext())){
                loadMenu();
            }else {
                Toast.makeText(HomeActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
            }
        });
        //Setup Slider
        setUpSlider();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.refreshMenu){
            loadMenu();
        }else if (id==R.id.addCategoryMenu){
            showDialogAddCategory();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.nav_log_out) {
            Intent signInIntent = new Intent(HomeActivity.this, MainActivity.class);
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signInIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }else if(id==R.id.nav_show_orders){
            startActivity(new Intent(HomeActivity.this,OrderStatusActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadMenu(){

        adapter.startListening();
        recyclerMenu.setAdapter(adapter);
        srlHome.setRefreshing(false);

        Objects.requireNonNull(recyclerMenu.getAdapter()).notifyDataSetChanged();
        recyclerMenu.scheduleLayoutAnimation();
        registerForContextMenu(recyclerMenu);
    }

    private void setUpSlider() {
        sliderLayout=findViewById(R.id.sliderHome);
        image_list=new HashMap<>();
        final DatabaseReference banners=database.getReference("Banner").child(Common.currentLanguage);
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Banner banner=dataSnapshot.getValue(Banner.class);
                    assert banner != null;
                    image_list.put(banner.getName()+"_"+banner.getId(),banner.getImage());
                }
                for (String key:image_list.keySet()){
                    String[] keySplit=key.split("_");
                    String foodName=keySplit[0];
                    final String foodID=keySplit[1];
                    //Create slider
                    final TextSliderView textSliderView=new TextSliderView(getBaseContext());
                    textSliderView
                            .description(foodName)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(slider -> {
                                Intent intent=new Intent(HomeActivity.this,FoodDetailActivity.class);
                                intent.putExtras(textSliderView.getBundle());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodID",foodID);
                    sliderLayout.addSlider(textSliderView);

                    //Remove event after finish
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
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
            editCategory(pos);
        }else if(title.equals(Common.DELETE)){
            deleteCategory(pos);
        }
        return super.onContextItemSelected(item);
    }


    private void editCategory(int categoryID) {
        LayoutInflater inflater=LayoutInflater.from(this);
        View layoutEditCategory=inflater.inflate(R.layout.add_category_layout,null);
        final MaterialEditText edtCategoryNameEn=layoutEditCategory.findViewById(R.id.edtCategoryNameEn);
        final MaterialEditText edtCategoryNameVi=layoutEditCategory.findViewById(R.id.edtCategoryNameVi);
        final MaterialEditText edtCategoryImageUrl=layoutEditCategory.findViewById(R.id.edtCategoryImageUrl);
        database.getReference("Category").child("en").orderByKey().equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    edtCategoryNameEn.setText(Objects.requireNonNull(dataSnapshot.getValue(Category.class)).getName());
                    edtCategoryImageUrl.setText(Objects.requireNonNull(dataSnapshot.getValue(Category.class)).getImage());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        database.getReference("Category").child("vi").orderByKey().equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    edtCategoryNameVi.setText(Objects.requireNonNull(dataSnapshot.getValue(Category.class)).getName());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("Edit category")
                .setMessage("Fill all information to edit !")
                .setView(layoutEditCategory)
                .setPositiveButton("Edit", (dialog, which) -> {
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(HomeActivity.this);
                    waitingDialog.show();
                    if (Objects.requireNonNull(edtCategoryNameEn.getText()).toString().trim().length()==0 || Objects.requireNonNull(edtCategoryNameVi.getText()).toString().trim().length()==0 ||Objects.requireNonNull(edtCategoryImageUrl.getText()).toString().trim().length()==0){
                        Toast.makeText(HomeActivity.this, "Please fill all information to edit !", Toast.LENGTH_SHORT).show();
                    } else {
                        Category categoryEn=new Category(edtCategoryNameEn.getText().toString(),edtCategoryImageUrl.getText().toString());
                        Category categoryVi=new Category(edtCategoryNameVi.getText().toString(),edtCategoryImageUrl.getText().toString());
                        database.getReference("Category").child("en").child(categoryID+"").setValue(categoryEn);
                        database.getReference("Category").child("vi").child(categoryID+"").setValue(categoryVi);
                        Toast.makeText(HomeActivity.this, "Edit category successfully !", Toast.LENGTH_SHORT).show();
                    }
                    waitingDialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        alertDialog.show();
    }

    private void deleteCategory(int categoryID) {
        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("Delete category")
                .setMessage("Are you sure to delete this category ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    database.getReference("Category").child("en").orderByKey().equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    database.getReference("Category").child("vi").orderByKey().equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    database.getReference("Food").child("en").orderByChild("categoryID").equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    database.getReference("Food").child("vi").orderByChild("categoryID").equalTo(categoryID+"").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Toast.makeText(this, "Delete category successfully !", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {

                })
                .create();
        alertDialog.show();
    }

    private void showDialogAddCategory() {
        LayoutInflater inflater=LayoutInflater.from(this);
        View layoutAddCategory=inflater.inflate(R.layout.add_category_layout,null);
        final MaterialEditText edtCategoryNameEn=layoutAddCategory.findViewById(R.id.edtCategoryNameEn);
        final MaterialEditText edtCategoryNameVi=layoutAddCategory.findViewById(R.id.edtCategoryNameVi);
        final MaterialEditText edtCategoryImageUrl=layoutAddCategory.findViewById(R.id.edtCategoryImageUrl);
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("Add new category")
                .setMessage("Fill all information to add !")
                .setView(layoutAddCategory)
                .setPositiveButton("Add", (dialog, which) -> {
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(HomeActivity.this);
                    waitingDialog.show();
                    if (Objects.requireNonNull(edtCategoryNameEn.getText()).toString().trim().length()==0 ||Objects.requireNonNull(edtCategoryNameVi.getText()).toString().trim().length()==0 || Objects.requireNonNull(edtCategoryImageUrl.getText()).toString().trim().length()==0){
                        Toast.makeText(HomeActivity.this, "Please fill all information to add !", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference categories = FirebaseDatabase.getInstance().getReference("Category");
                        categories.child("en").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    int lastKey =Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                                    Category categoryEn=new Category(edtCategoryNameEn.getText().toString(),edtCategoryImageUrl.getText().toString());
                                    categories.child("en").child(String.valueOf(lastKey+1)).setValue(categoryEn);
                                    Toast.makeText(HomeActivity.this, "Add category successfully !", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                throw error.toException();
                            }
                        });
                        categories.child("vi").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    int lastKey =Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                                    Category categoryVi=new Category(edtCategoryNameVi.getText().toString(),edtCategoryImageUrl.getText().toString());
                                    categories.child("vi").child(String.valueOf(lastKey+1)).setValue(categoryVi);
                                    Toast.makeText(HomeActivity.this, "Add category successfully !", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                throw error.toException();
                            }
                        });
                    }
                    waitingDialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
        recyclerMenu.startAnimation(controller.getAnimation());
        sliderLayout.startAutoCycle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        sliderLayout.stopAutoCycle();
        //FirebaseAuth.getInstance().signOut();
    }
}