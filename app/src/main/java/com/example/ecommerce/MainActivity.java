package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.adapters.CategoryAdapter;
import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.db.DatabaseHelper;
import com.example.ecommerce.models.Category;
import com.example.ecommerce.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private FloatingActionButton fabCart;
    private FloatingActionButton fabChat;
    private DatabaseHelper dbHelper;
    private int selectedCategoryId = -1; // -1 means all products

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        rvCategories = findViewById(R.id.rv_categories);
        rvProducts = findViewById(R.id.rv_products);
        fabCart = findViewById(R.id.fab_cart);
        fabChat = findViewById(R.id.fab_chat);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set up categories RecyclerView
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Category> categories = dbHelper.getAllCategories();
        categoryAdapter = new CategoryAdapter(categories);
        categoryAdapter.setOnItemClickListener(position -> {
            Category category = categories.get(position);
            selectedCategoryId = category.getId();
            loadProductsByCategory(selectedCategoryId);
            Toast.makeText(MainActivity.this, "Category: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        rvCategories.setAdapter(categoryAdapter);

        // Set up products RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        loadAllProducts();

        // Set up FABs
        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });
    }

    private void loadAllProducts() {
        List<Product> products = dbHelper.getAllProducts();
        setupProductAdapter(products);
    }

    private void loadProductsByCategory(int categoryId) {
        List<Product> products = dbHelper.getProductsByCategory(categoryId);
        setupProductAdapter(products);
    }

    private void setupProductAdapter(List<Product> products) {
        productAdapter = new ProductAdapter(products);
        productAdapter.setOnItemClickListener(position -> {
            Product product = products.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    performSearch(newText);
                } else if (newText.isEmpty()) {
                    if (selectedCategoryId == -1) {
                        loadAllProducts();
                    } else {
                        loadProductsByCategory(selectedCategoryId);
                    }
                }
                return true;
            }
        });
        
        return true;
    }

    private void performSearch(String query) {
        List<Product> searchResults = dbHelper.searchProducts(query);
        setupProductAdapter(searchResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_cart) {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            selectedCategoryId = -1;
            loadAllProducts();
        } else if (id == R.id.nav_categories) {
            // Show categories dialog or navigate to categories screen
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
            startActivity(intent);
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
    protected void onResume() {
        super.onResume();
        // Refresh products when returning to this activity
        if (selectedCategoryId == -1) {
            loadAllProducts();
        } else {
            loadProductsByCategory(selectedCategoryId);
        }
    }
}
