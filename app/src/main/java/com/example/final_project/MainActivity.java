package com.example.final_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private ViewPager2 viewPagerBanner;
    private BannerAdapter bannerAdapter;
    private List<String> bannerUrls;

    private ItemAdapter itemAdapter;
    private List<FoodProduct> itemList;

    private ProgressBar progressBarPopular;
    private ListView listViewProducts;

    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private int currentBannerPosition = 0;
    private static final int BANNER_SWITCH_DELAY = 10000; // 10 seconds
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // User is not signed in, redirect to login activity
            Intent intent = new Intent(this, UserLogin.class);
            startActivity(intent);
            finish();
            return; // Exit onCreate to avoid running the rest of the code
        }

        // Set up the custom Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.list_arrow);
        getSupportActionBar().setTitle("Hello Foods");

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up ActionBarDrawerToggle to handle drawer opening/closing
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Enable the home button to open/close the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up NavigationView's item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item);
            return true;
        });

        // Set up the ViewPager2 and BannerAdapter for displaying banners
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        bannerUrls = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerUrls);
        viewPagerBanner.setAdapter(bannerAdapter);

        // Initialize ProgressBar for popular items
        progressBarPopular = findViewById(R.id.progressBarPopular);

        // Initialize RecyclerView for items
        RecyclerView recycleviewPopular = findViewById(R.id.recycleviewPopular);
        recycleviewPopular.setLayoutManager(new GridLayoutManager(this, 2));  // 2 items per row in grid
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList);
        recycleviewPopular.setAdapter(itemAdapter);

        // Fetch items and banners from Firebase
        fetchItemsFromFirebase();
        //fetchBannersFromFirebase();

        // Initialize ListView for products added in AddFoodProductsActivity
        listViewProducts = findViewById(R.id.listViewProducts); // Corrected to the actual ListView ID

        // Display products added from AddFoodProductsActivity
        displayAddedProducts();
    }

    private void displayAddedProducts() {
        List<String> productDetailsList = new ArrayList<>();
        if(AddFoodProductsActivity.foodProductList != null)
            for (FoodProduct product : AddFoodProductsActivity.foodProductList) {
                String details = "Name: " + product.getName() + "\n" +
                        "Price: " + product.getPrice() + "\n" +
                        "Description: " + product.getDescription() + "\n" +
                        "Quantity: " + product.getQuantity();
                productDetailsList.add(details);
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productDetailsList);
        listViewProducts.setAdapter(adapter);
    }

    // Method to handle navigation item selection using if-else statements
    private void handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id != R.id.nav_home) {
            if (id == R.id.nav_profile) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
            } else if (id == R.id.nav_my_order) {
                intent = new Intent(MainActivity.this, OrderActivity.class);
            } else if (id == R.id.nav_my_cart) {
                intent = new Intent(MainActivity.this, CartActivity.class);
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
                intent = new Intent(MainActivity.this, UserLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
                return;
            }
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void fetchItemsFromFirebase() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("food_items");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodProduct item = snapshot.getValue(FoodProduct.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                itemAdapter.updateItemList(itemList); // Update the RecyclerView with new data

                // Hide the ProgressBar once the data is loaded
                progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                progressBarPopular.setVisibility(View.GONE); // Hide the ProgressBar even if there’s an error
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (bannerHandler != null && bannerRunnable != null) {
//            bannerHandler.removeCallbacks(bannerRunnable); // Stop the auto-scrolling when the activity is destroyed
//        }
    }
}
