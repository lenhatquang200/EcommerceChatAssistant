package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.db.DatabaseHelper;
import com.example.ecommerce.models.Product;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private DatabaseHelper dbHelper;
    private int categoryId = -1;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        SearchView searchView = findViewById(R.id.search_view);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get extras
        if (getIntent().hasExtra("category_id")) {
            categoryId = getIntent().getIntExtra("category_id", -1);
            String categoryName = getIntent().getStringExtra("category_name");
            if (categoryName != null) {
                getSupportActionBar().setTitle(categoryName);
            }
        } else if (getIntent().hasExtra("search_query")) {
            searchQuery = getIntent().getStringExtra("search_query");
            getSupportActionBar().setTitle("Search Results: " + searchQuery);
            searchView.setQuery(searchQuery, false);
        }

        // Set up RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                loadProducts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    searchQuery = newText;
                    loadProducts();
                } else if (newText.isEmpty()) {
                    searchQuery = "";
                    loadProducts();
                }
                return true;
            }
        });

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        showLoading();

        // Retrieve products based on category or search query
        List<Product> products;
        if (!searchQuery.isEmpty()) {
            products = dbHelper.searchProducts(searchQuery);
        } else if (categoryId != -1) {
            products = dbHelper.getProductsByCategory(categoryId);
        } else {
            products = dbHelper.getAllProducts();
        }

        // Update UI
        if (products.isEmpty()) {
            showEmptyState();
        } else {
            showProducts(products);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        rvProducts.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        
        if (!searchQuery.isEmpty()) {
            tvEmptyState.setText(getString(R.string.no_search_results, searchQuery));
        } else {
            tvEmptyState.setText(R.string.no_products_available);
        }
    }

    private void showProducts(List<Product> products) {
        progressBar.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        adapter = new ProductAdapter(products);
        adapter.setOnItemClickListener(position -> {
            Product product = products.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
